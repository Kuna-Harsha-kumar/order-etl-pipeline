package com.example.etl.producer;

import com.example.etl.dto.OrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates an upstream source system continuously emitting new orders.
 * In a real pipeline this class would be replaced by an API poller,
 * a file watcher, or a Debezium/CDC source instead.
 */
@Component
public class OrderGeneratorScheduler {

    private static final List<String> COUNTRIES = List.of("US", "GB", "DE", "IN", "BR", "JP");
    private static final List<String> PRODUCTS = List.of(
            "Wireless Mouse", "Mechanical Keyboard", "USB-C Hub", "27in Monitor",
            "Noise Cancelling Headphones", "Webcam 1080p", "Laptop Stand", "Desk Lamp"
    );

    private final OrderProducer orderProducer;
    private final boolean enabled;

    public OrderGeneratorScheduler(OrderProducer orderProducer,
                                    @Value("${app.demo.generator.enabled:true}") boolean enabled) {
        this.orderProducer = orderProducer;
        this.enabled = enabled;
    }

    @Scheduled(fixedRateString = "${app.demo.generator.interval-ms:3000}")
    public void generateOrder() {
        if (!enabled) {
            return;
        }
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                "cust-" + rnd.nextInt(1, 51),
                COUNTRIES.get(rnd.nextInt(COUNTRIES.size())),
                "prod-" + rnd.nextInt(1, PRODUCTS.size() + 1),
                PRODUCTS.get(rnd.nextInt(PRODUCTS.size())),
                rnd.nextInt(1, 6),
                BigDecimal.valueOf(rnd.nextDouble(9.99, 299.99)).setScale(2, java.math.RoundingMode.HALF_UP),
                Instant.now()
        );

        orderProducer.publish(event);
    }
}
