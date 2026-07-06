package com.gasfgrv.autorizador.core.infrastructure.mappers;

import com.gasfgrv.autorizador.core.domain.entities.Resposta;
import com.gasfgrv.autorizador.core.infrastructure.dtos.TopicoEventoPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventoMapper {

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "clienteId", target = "customerId")
    @Mapping(source = "dataPedido", target = "orderDate")
    @Mapping(source = "total", target = "amount")
    @Mapping(source = "nomeCartao", target = "nameOnCard")
    @Mapping(source = "numeroCartao", target = "creditCardNumber")
    @Mapping(source = "expiraEm", target = "expiry")
    @Mapping(source = "cvv", target = "cvv")
    @Mapping(source = "aprovado", target = "approved")
    TopicoEventoPayload toEventoPayload(Resposta resposta);

}
