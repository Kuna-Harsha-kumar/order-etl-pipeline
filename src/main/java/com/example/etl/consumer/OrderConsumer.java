package com.example.etl.consumer;

import com.example.etl.dto.OrderEvent;
import com.example.etl.dto.ProcessedOrderEvent;
import com.example.etl.model.ProcessedOrder;
import com.example.etl.repository.ProcessedOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;

/**
 * Transform + Load stage of the pipeline.
 * Consumes raw order events, applies business rules (tier calculation,
 * currency conversion), persists the result, and republishes the
 * enriched record for any downstream consumers.
 */
@Component
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    // Mock exchange rates (USD -> local currency). In production this would
    // come from a rates API or a reference table refreshed periodically.
    private static final Map<String, String> COUNTRY_CURRENCY = Map.of(
            "US", "USD", "GB", "GBP", "DE", "EUR", "IN", "INR", "BR", "BRL", "JP", "JPY"
    );
    private static final Map<String, BigDecimal> USD_EXCHANGE_RATES = Map.of(
            "USD", BigDecimal.ONE,
            "GBP", BigDecimal.valueOf(0.78),
            "EUR", BigDecimal.valueOf(0.92),
            "INR", BigDecimal.valueOf(83.10),
            "BRL", BigDecimal.valueOf(5.40),
            "JPY", BigDecimal.valueOf(149.50)
    );

    private final ProcessedOrderRepository repository;
    private final KafkaTemplate<String, ProcessedOrderEvent> processedOrderKafkaTemplate;
    private final String processedTopic;

    public OrderConsumer(ProcessedOrderRepository repository,
                          KafkaTemplate<String, ProcessedOrderEvent> processedOrderKafkaTemplate,
                          @Value("${app.kafka.topic.orders-processed}") String processedTopic) {
        this.repository = repository;
        this.processedOrderKafkaTemplate = processedOrderKafkaTemplate;
        this.processedTopic = processedTopic;
    }

    @KafkaListener(topics = "${app.kafka.topic.orders-raw}", groupId = "order-etl-consumer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(OrderEvent event) {
        try {
            ProcessedOrder saved = transformAndLoad(event);
            publishProcessedEvent(saved);
            log.info("Processed order {} for customer {} -> tier={}, totalUsd={}",
                    saved.getOrderId(), saved.getCustomerId(), saved.getCustomerTier(), saved.getTotalPriceUsd());
        } catch (Exception ex) {
            // In production, route to a dead-letter topic (see app.kafka.topic.orders-dlq)
            // instead of just logging. Kept simple here for readability.
            log.error("Failed to process order {}: {}", event.getOrderId(), ex.getMessage(), ex);
        }
    }

    private ProcessedOrder transformAndLoad(OrderEvent event) {
        BigDecimal totalUsd = event.getUnitPriceUsd()
                .multiply(BigDecimal.valueOf(event.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        String currency = COUNTRY_CURRENCY.getOrDefault(event.getCustomerCountry(), "USD");
        BigDecimal rate = USD_EXCHANGE_RATES.getOrDefault(currency, BigDecimal.ONE);
        BigDecimal totalLocal = totalUsd.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        String tier = calculateTier(totalUsd);

        ProcessedOrder processed = new ProcessedOrder();
        processed.setOrderId(event.getOrderId());
        processed.setCustomerId(event.getCustomerId());
        processed.setCustomerCountry(event.getCustomerCountry());
        processed.setCustomerTier(tier);
        processed.setProductName(event.getProductName());
        processed.setQuantity(event.getQuantity());
        processed.setTotalPriceUsd(totalUsd);
        processed.setTotalPriceLocal(totalLocal);
        processed.setLocalCurrency(currency);
        processed.setOrderCreatedAt(event.getCreatedAt());
        processed.setProcessedAt(Instant.now());

        return repository.save(processed);
    }

    private String calculateTier(BigDecimal totalUsd) {
        if (totalUsd.compareTo(BigDecimal.valueOf(500)) >= 0) {
            return "PLATINUM";
        } else if (totalUsd.compareTo(BigDecimal.valueOf(150)) >= 0) {
            return "GOLD";
        } else {
            return "SILVER";
        }
    }

    private void publishProcessedEvent(ProcessedOrder saved) {
        ProcessedOrderEvent event = new ProcessedOrderEvent(
                saved.getOrderId(), saved.getCustomerId(), saved.getCustomerCountry(), saved.getCustomerTier(),
                saved.getProductName(), saved.getQuantity(), saved.getTotalPriceUsd(), saved.getTotalPriceLocal(),
                saved.getLocalCurrency(), saved.getOrderCreatedAt(), saved.getProcessedAt()
        );
        processedOrderKafkaTemplate.send(processedTopic, saved.getCustomerId(), event);
    }
}
