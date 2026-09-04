package com.example.etl.producer;

import com.example.etl.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final String topic;

    public OrderProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate,
                          @Value("${app.kafka.topic.orders-raw}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(OrderEvent event) {
        // Key by customerId so all of a customer's orders land on the same partition
        // (preserves per-customer ordering).
        kafkaTemplate.send(topic, event.getCustomerId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish order {}: {}", event.getOrderId(), ex.getMessage(), ex);
                    } else {
                        log.info("Published order {} to partition {}", event.getOrderId(),
                                result.getRecordMetadata().partition());
                    }
                });
    }
}
