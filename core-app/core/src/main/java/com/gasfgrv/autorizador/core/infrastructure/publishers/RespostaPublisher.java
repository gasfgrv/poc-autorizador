package com.gasfgrv.autorizador.core.infrastructure.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.autorizador.core.domain.entities.Resposta;
import com.gasfgrv.autorizador.core.domain.ports.out.RespostaSenderPort;
import com.gasfgrv.autorizador.core.infrastructure.dtos.TopicoEventoPayload;
import com.gasfgrv.autorizador.core.infrastructure.mappers.EventoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RespostaPublisher implements RespostaSenderPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EventoMapper eventoMapper;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.publisher.name}")
    private String publisherTopic;

    @Override
    public void enviarResposta(Resposta resposta, String taskToken) {
        try {
            TopicoEventoPayload eventoPayload = eventoMapper.toEventoPayload(resposta);
            String kafkaMessage = objectMapper.writeValueAsString(eventoPayload);

            Message<String> message = MessageBuilder
                    .withPayload(kafkaMessage)
                    .setHeader("taskToken", taskToken.getBytes())
                    .setHeader(KafkaHeaders.TOPIC, publisherTopic)
                    .build();

            log.info("Enviando resposta para o tópico: {}", kafkaMessage);
            kafkaTemplate.send(message);
        } catch (Exception e) {
            log.error("Erro ao enviar resposta: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
