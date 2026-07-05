package com.gasfgrv.autorizador.events.infrastructure.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.autorizador.events.domain.ports.in.AutorizadorInputPort;
import com.gasfgrv.autorizador.events.infrastructure.dtos.kafka.TopicoEventoPayload;
import com.gasfgrv.autorizador.events.infrastructure.mappers.PedidoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RespostaAutorizadorListener {

    private final ObjectMapper objectMapper;
    private final AutorizadorInputPort autorizador;
    private final PedidoMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic.consumer.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload String data, @Headers Map<String, Object> headers, Acknowledgment ack) {
        try {
            TopicoEventoPayload payload = objectMapper.readValue(data, TopicoEventoPayload.class);

            if (!headers.containsKey("taskToken")) {
                log.warn("Mensagem recebida sem taskToken: {}", data);
            }

            String taskToken = headers.getOrDefault("taskToken", "").toString();
            autorizador.enviarResposta(mapper.toDomain(payload), payload.approved(), taskToken);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem do tópico: {}", data, e);
            throw new KafkaException(e);
        } finally {
            ack.acknowledge();
        }
    }

}
