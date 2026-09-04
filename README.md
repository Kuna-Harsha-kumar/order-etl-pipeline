# Order ETL Pipeline (Spring Boot + Kafka + Postgres)

A ready-to-run ETL pipeline demonstrating a real-world pattern: **Extract** (produce raw
events) → **Transform** (enrich/clean via a Kafka consumer) → **Load** (persist to Postgres),
with the enriched result also republished to a downstream Kafka topic.

## Architecture

```
                 ┌────────────────────────┐
  (scheduler /   │   OrderGeneratorScheduler│
   REST API)     │   OrderController (POST) │
                 └───────────┬─────────────┘
                              │ publish
                              ▼
                     Kafka topic: orders-raw
                              │
                              ▼
                 ┌─────────────────────────┐
                 │      OrderConsumer        │
                 │  - compute totals          │
                 │  - currency conversion     │
                 │  - customer tier rule       │
                 └───────────┬─────────────┘
                       │ save            │ publish
                       ▼                 ▼
              Postgres: processed_orders   Kafka topic: orders-processed
```

- **Extract**: `OrderGeneratorScheduler` simulates a source system emitting a new order every
  few seconds. `OrderController` (`POST /api/orders`) lets you submit orders manually — swap
  either for a real upstream (REST poller, file watcher, Debezium/CDC connector, etc.).
- **Transform**: `OrderConsumer` consumes from `orders-raw`, computes order totals, converts to
  the customer's local currency, and assigns a loyalty tier (SILVER/GOLD/PLATINUM).
- **Load**: the transformed record is saved to the `processed_orders` table in Postgres via
  Spring Data JPA, and republished to `orders-processed` for any downstream consumer.

## Stack

Spring Boot 3.3 · Spring Kafka · Spring Data JPA · PostgreSQL 16 · Apache Kafka (KRaft mode,
no Zookeeper) · Docker Compose · Kafka UI

## Run it (Docker Compose — recommended)

Requires Docker and Docker Compose.

```bash
docker compose up --build
```

This starts:
| Service    | Port  | Purpose                          |
|------------|-------|-----------------------------------|
| `app`      | 8080  | Spring Boot ETL pipeline          |
| `kafka`    | 9092/9094 | Kafka broker (KRaft, single node) |
| `postgres` | 5432  | Load target database              |
| `kafka-ui` | 8081  | Web UI to inspect topics/messages |

Within a few seconds you'll see orders flowing through the logs (the demo generator publishes
one every 3 seconds by default). Open **http://localhost:8081** to watch messages land on
`orders-raw` and `orders-processed` in real time.

To stop and wipe all data:
```bash
docker compose down -v
```

## Run it locally (without Docker for the app)

1. Start just the infra: `docker compose up kafka postgres kafka-ui`
2. **Wait until Postgres and Kafka report healthy** (`docker compose ps` should show both as
   `healthy`, usually 10-20 seconds). Starting the app before they're ready is the #1 cause of
   the `Unable to determine Dialect without JDBC metadata` error below.
3. Run the app: `./mvnw spring-boot:run` (Linux/Mac) or `mvnw.cmd spring-boot:run` (Windows) —
   no local Maven install needed, the wrapper downloads it for you on first run.

## Troubleshooting: "Unable to determine Dialect without JDBC metadata"

This error means Hibernate could not open a JDBC connection at all — it's a **connectivity**
problem, not a config-syntax problem. Check, in order:

1. **Is Postgres actually running and reachable?**
   ```bash
   docker compose ps          # postgres should say "healthy"
   docker compose logs postgres
   ```
2. **Did you start the app before Postgres finished initializing?** Wait for the healthcheck,
   then start/restart the app.
3. **Are the credentials/URL in `application.properties` correct for how you're running it?**
   - Running the app **outside** Docker → it must reach Postgres at `localhost:5432`
     (`application.properties`).
   - Running the app **inside** Docker Compose → it must reach Postgres at `postgres:5432`
     and needs `SPRING_PROFILES_ACTIVE=docker` set (already set in `docker-compose.yml` for
     the `app` service) so `application-docker.properties` is applied.
   - Mixing these up (e.g. running the jar locally but pointing it at `postgres:5432`, which
     only resolves inside the Compose network) causes exactly this failure.
4. **Test the connection directly:**
   ```bash
   docker exec -it etl-postgres psql -U etl_user -d orders_db -c "SELECT 1;"
   ```
   If this fails, the issue is with Postgres itself (check `docker compose logs postgres`),
   not with the Spring app.

## Try it out

**Submit an order manually:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
        "customerId": "cust-42",
        "customerCountry": "DE",
        "productId": "prod-7",
        "productName": "Mechanical Keyboard",
        "quantity": 2,
        "unitPriceUsd": 89.99
      }'
```

**Check the load target (paginated):**
```bash
curl http://localhost:8080/api/orders/processed?page=0&size=10
```

**Look up one order:**
```bash
curl http://localhost:8080/api/orders/processed/{orderId}
```

**Filter by customer:**
```bash
curl http://localhost:8080/api/orders/processed/by-customer/cust-42
```

**Health check:**
```bash
curl http://localhost:8080/actuator/health
```

## Turning off the demo generator

The scheduler that auto-generates fake orders can be disabled so only manually-submitted
orders flow through the pipeline. In `application.yml` (or via env var in Compose):

```yaml
app:
  demo:
    generator:
      enabled: false
```

or `SPRING_APPLICATION_JSON='{"app":{"demo":{"generator":{"enabled":false}}}}'` /
`-Dapp.demo.generator.enabled=false` when running the jar directly.

## Extending this into a bigger project

- **Dead-letter handling**: route failed messages to the pre-created `orders-dlq` topic
  instead of just logging (there's a `DefaultErrorHandler` extension point in `KafkaConfig`).
- **Schema Registry + Avro**: swap `JsonSerializer`/`JsonDeserializer` for Avro with Confluent
  Schema Registry to get schema evolution safety.
- **Kafka Streams**: replace the simple `@KafkaListener` transform with a `KStream` topology
  for windowed aggregations (e.g. rolling 5-minute revenue per country).
- **CDC source**: point Debezium at a real orders table instead of the scheduler to capture
  real database changes instead of simulated ones.
- **Batch/ELT variant**: add a nightly Spring Batch job that reads `processed_orders` and
  rolls it up into a `daily_sales_summary` table — a classic ELT-style aggregation step.
- **Observability**: the `/actuator` endpoints are already exposed — wire up
  Prometheus + Grafana for metrics, and Micrometer tracing for end-to-end latency per order.

## Project structure

```
order-etl-pipeline/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── src/main/java/com/example/etl/
│   ├── EtlPipelineApplication.java
│   ├── config/          # Kafka topic + producer/consumer factory config
│   ├── dto/              # Kafka message payloads + REST request DTO
│   ├── model/            # JPA entity (load target)
│   ├── producer/          # Extract stage (scheduler + Kafka producer)
│   ├── consumer/          # Transform + Load stage (Kafka listener)
│   ├── repository/        # Spring Data JPA repository
│   └── controller/        # REST API (manual extract + read/verify endpoints)
└── src/main/resources/
    ├── application.properties         # local/default config
    └── application-docker.properties  # docker-compose profile overrides
```
