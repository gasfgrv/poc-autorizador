package com.gasfgrv.autorizador.core.infrastructure.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.autorizador.core.domain.ports.in.AutorizadorResponsePort;
import com.gasfgrv.autorizador.core.infrastructure.dtos.TopicoComandoPayload;
import com.gasfgrv.autorizador.core.infrastructure.exceptions.ListenerException;
import com.gasfgrv.autorizador.core.infrastructure.mappers.ComandoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComandoListener {

    private final ObjectMapper objectMapper;
    private final AutorizadorResponsePort usecase;
    private final ComandoMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic.consumer.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload String data, @Headers Map<String, byte[]> headers, Acknowledgment ack) {
        try {
            log.info("Comando recebido: {}", data);
            TopicoComandoPayload payload = objectMapper.readValue(data, TopicoComandoPayload.class);

            if (!headers.containsKey("taskToken")) {
                log.warn("Mensagem recebida sem taskToken: {}", data);
                return;
            }

            String taskToken = new String(headers.get("taskToken"), StandardCharsets.UTF_8);
            usecase.autorizar(mapper.toDomain(payload), taskToken);
        } catch (Exception e) {
            log.error("Erro ao processar comando do tópico: {}", data, e);
            throw new ListenerException(e);
        } finally {
            ack.acknowledge();
        }
    }

}
