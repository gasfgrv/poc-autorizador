package com.gasfgrv.autorizador.core.infrastructure.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.autorizador.core.containers.TestcontainersConfiguration;
import com.gasfgrv.autorizador.core.domain.entities.Pedido;
import com.gasfgrv.autorizador.core.domain.ports.in.AutorizadorResponsePort;
import com.gasfgrv.autorizador.core.infrastructure.dtos.TopicoComandoPayload;
import com.gasfgrv.autorizador.core.infrastructure.mappers.ComandoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ExtendWith(OutputCaptureExtension.class)
class ComandoListenerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockitoSpyBean
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private AutorizadorResponsePort usecase;

    @MockitoSpyBean
    private ComandoMapper mapper;

    private String taskToken;

    @BeforeEach
    void setUp() {
        this.taskToken = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("Listener deve consumir uma mensagem com sucesso")
    void listenerDeveConsumirUmaMensagemComSucesso(CapturedOutput output) {


        Message<String> message = MessageBuilder
                .withPayload(getPayload())
                .setHeader("taskToken", taskToken.getBytes())
                .setHeader(KafkaHeaders.TOPIC, "autorizador-comando")
                .build();

        kafkaTemplate.send(message);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    assertFalse(output.getOut().contains("Mensagem recebida sem taskToken"));
                    verify(objectMapper).readValue(anyString(), eq(TopicoComandoPayload.class));
                    verify(usecase).autorizar(any(Pedido.class), eq(taskToken));
                    verify(mapper).toDomain(any(TopicoComandoPayload.class));
                });
    }

    @Test
    @DisplayName("Listener deve consumir uma mensagem sem header de task token")
    void listenerDeveConsumirUmaMensagemSemHeaderDeTaskToken(CapturedOutput output) {
        Message<String> message = MessageBuilder
                .withPayload(getPayload())
                .setHeader(KafkaHeaders.TOPIC, "autorizador-comando")
                .build();

        kafkaTemplate.send(message);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    assertTrue(output.getOut().contains("Mensagem recebida sem taskToken"));
                    verify(objectMapper).readValue(anyString(), eq(TopicoComandoPayload.class));
                    verify(usecase, never()).autorizar(any(Pedido.class), eq(taskToken));
                    verify(mapper, never()).toDomain(any(TopicoComandoPayload.class));
                });
    }

    private String getPayload() {
        TopicoComandoPayload payload = new TopicoComandoPayload(
                "1234567",
                "98766",
                LocalDate.of(2024, 1, 14),
                BigDecimal.valueOf(100L),
                "FIRSTNAME LASTNAME",
                "1234 1234 1234 1234",
                "XX/YY",
                "123"
        );

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}