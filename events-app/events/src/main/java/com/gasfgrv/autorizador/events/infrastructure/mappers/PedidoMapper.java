package com.gasfgrv.autorizador.events.infrastructure.mappers;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.infrastructure.dtos.sqs.SqsEventPayloadDto;
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
    Pedido toDomain(SqsEventPayloadDto payload);

}
