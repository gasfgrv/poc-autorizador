package com.gasfgrv.autorizador.events.infrastructure.mappers;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.infrastructure.dtos.kafka.TopicoComandoPayload;
import com.gasfgrv.autorizador.events.infrastructure.dtos.kafka.TopicoEventoPayload;
import com.gasfgrv.autorizador.events.infrastructure.dtos.sqs.EventDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(source = "orderId", target = "id")
    @Mapping(source = "customerId", target = "clienteId")
    @Mapping(source = "orderDate", target = "dataPedido")
    @Mapping(source = "amount", target = "total")
    @Mapping(source = "nameOnCard", target = "nomeCartao")
    @Mapping(source = "creditCardNumber", target = "numeroCartao")
    @Mapping(source = "expiry", target = "expiraEm")
    Pedido toDomain(EventDetailsDto details);

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "clienteId", target = "customerId")
    @Mapping(source = "dataPedido", target = "orderDate")
    @Mapping(source = "total", target = "amount")
    @Mapping(source = "nomeCartao", target = "nameOnCard")
    @Mapping(source = "numeroCartao", target = "creditCardNumber")
    @Mapping(source = "expiraEm", target = "expiry")
    TopicoComandoPayload toCommandPayload(Pedido pedido);

    @Mapping(source = "orderId", target = "id")
    @Mapping(source = "customerId", target = "clienteId")
    @Mapping(source = "orderDate", target = "dataPedido")
    @Mapping(source = "amount", target = "total")
    @Mapping(source = "nameOnCard", target = "nomeCartao")
    @Mapping(source = "creditCardNumber", target = "numeroCartao")
    @Mapping(source = "expiry", target = "expiraEm")
    Pedido toDomain(TopicoEventoPayload payload);

}
