package com.gasfgrv.autorizador.events.infrastructure.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;

import java.net.URI;

@Configuration
public class StepFunctionConfiguration {

    @Bean
    public SfnAsyncClient sfnAsyncClient(@Value("${spring.cloud.aws.region.static}") String region,
                                         @Value("${spring.cloud.aws.stepfunction.endpoint}") String endpoint) {
        return SfnAsyncClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .build();
    }

}
