package com.gasfgrv.autorizador.events.infrastructure.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.ports.out.EventPublisherPort;
import com.gasfgrv.autorizador.events.infrastructure.dtos.kafka.TopicoComandoPayload;
import com.gasfgrv.autorizador.events.infrastructure.mappers.PedidoMapper;
import com.gasfgrv.autorizador.events.infrastructure.properties.KafkaPublisherProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisherAdapter implements EventPublisherPort {

    private final PedidoMapper pedidoMapper;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaPublisherProperties publisherProperties;

    @Override
    public void enviarEvento(Pedido pedido) {
        try {
            TopicoComandoPayload payload = pedidoMapper.toCommandPayload(pedido);
            String kafkaMessage = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(publisherProperties.name(), kafkaMessage);
        } catch (Exception e) {
            log.error("Erro ao emitir pedido: {}", e.getMessage());
            throw new KafkaException(e);
        }
    }

}
