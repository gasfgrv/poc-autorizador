package com.gasfgrv.autorizador.events.infrastructure.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class SqsConfiguration {

    @Bean
    public SqsAsyncClient sqsAsyncClient(@Value("${spring.cloud.aws.sqs.endpoint}") String endpoint,
                                         @Value("${spring.cloud.aws.region.static}") String region) {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .build();
    }

}
