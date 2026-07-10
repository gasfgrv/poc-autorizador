package com.gasfgrv.autorizador.events.containers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));
    }

    @Bean(destroyMethod = "stop")
    public LocalStackContainer localStackContainer() {
        try {
            LocalStackContainer container = new LocalStackContainer(DockerImageName
                    .parse("localstack/localstack:4.14.0"))
                    .withServices("sqs", "dynamodb", "stepfunctions");
            container.start();
            container.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "fila-teste-autorizador");
            return container;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Falha ao criar a fila de testes no LocalStack", e);
        }
    }

    @Bean
    public DynamicPropertyRegistrar awsPropertiesRegistrar(LocalStackContainer localStackContainer) {
        String endpoint = localStackContainer.getEndpoint().toString();
        return registry -> {
            registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
            registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
            registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
            registry.add("spring.cloud.aws.sqs.endpoint", () -> endpoint);
            registry.add("spring.cloud.aws.dynamodb.endpoint", () -> endpoint);
            registry.add("spring.cloud.aws.stepfunction.endpoint", () -> endpoint);
        };
    }

}
