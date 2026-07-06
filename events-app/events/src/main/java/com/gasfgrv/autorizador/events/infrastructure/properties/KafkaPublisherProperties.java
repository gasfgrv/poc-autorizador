package com.gasfgrv.autorizador.events.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.topic.publisher")
public record KafkaPublisherProperties(
        String name
) { }
