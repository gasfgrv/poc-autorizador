package com.gasfgrv.autorizador.events.infrastructure.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.autorizador.events.containers.TestcontainersConfiguration;
import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.ports.in.AutorizadorInputPort;
import com.gasfgrv.autorizador.events.infrastructure.dtos.kafka.TopicoEventoPayload;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {"spring.kafka.consumer.auto-offset-reset=earliest"})
class RespostaAutorizadorListenerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AutorizadorInputPort autorizadorInputPort;

    @Value("${spring.kafka.topic.consumer.name}")
    private String topicoConsumidor;

    @Test
    void listernerDeveProcessarMensagemComSucessoQuandoPayloadEHeadersForemValidos() {
        String taskToken = UUID.randomUUID().toString();
        String jsonPayload = gerarPayload();

        ProducerRecord<String, String> record = new ProducerRecord<>(topicoConsumidor, jsonPayload);
        record.headers().add(new RecordHeader("taskToken", taskToken.getBytes(StandardCharsets.UTF_8)));

        kafkaTemplate.send(record).join();

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(autorizadorInputPort)
                        .enviarResposta(any(Pedido.class), eq(true), eq(taskToken)));
    }

    @Test
    void listenerDeveIgnorarMensagemQuandoNaoPossuirTaskTokenNoHeader() {
        String jsonPayload = gerarPayload();

        ProducerRecord<String, String> record = new ProducerRecord<>(topicoConsumidor, jsonPayload);

        kafkaTemplate.send(record).join();

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(autorizadorInputPort, never())
                        .enviarResposta(any(Pedido.class), eq(true), anyString()));
    }

    private String gerarPayload() {
        try {
            TopicoEventoPayload payloadFake = new TopicoEventoPayload(
                    "1234567",
                    "98766",
                    LocalDate.of(2024, 1, 14),
                    BigDecimal.valueOf(100L),
                    "FIRSTNAME LASTNAME",
                    "1234 1234 1234 1234",
                    "XX/YY",
                    "123",
                    true
            );

            return objectMapper.writeValueAsString(payloadFake);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
            return null;
        }
    }

}