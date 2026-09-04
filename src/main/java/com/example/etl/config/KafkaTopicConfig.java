package com.example.etl.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.orders-raw}")
    private String ordersRawTopic;

    @Value("${app.kafka.topic.orders-processed}")
    private String ordersProcessedTopic;

    @Value("${app.kafka.topic.orders-dlq}")
    private String ordersDlqTopic;

    @Bean
    public NewTopic ordersRawTopic() {
        return TopicBuilder.name(ordersRawTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersProcessedTopic() {
        return TopicBuilder.name(ordersProcessedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersDlqTopic() {
        return TopicBuilder.name(ordersDlqTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
