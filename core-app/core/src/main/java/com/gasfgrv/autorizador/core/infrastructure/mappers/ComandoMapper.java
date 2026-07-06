package com.gasfgrv.autorizador.core.infrastructure.mappers;

import com.gasfgrv.autorizador.core.domain.entities.Pedido;
import com.gasfgrv.autorizador.core.infrastructure.dtos.TopicoComandoPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ComandoMapper {

    @Mapping(source = "orderId", target = "id")
    @Mapping(source = "customerId", target = "clienteId")
    @Mapping(source = "orderDate", target = "dataPedido")
    @Mapping(source = "amount", target = "total")
    @Mapping(source = "nameOnCard", target = "nomeCartao")
    @Mapping(source = "creditCardNumber", target = "numeroCartao")
    @Mapping(source = "expiry", target = "expiraEm")
    Pedido toDomain(TopicoComandoPayload payload);

}
