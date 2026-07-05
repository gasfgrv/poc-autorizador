package com.gasfgrv.autorizador.events.infrastructure.dtos.kafka;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TopicoComandoPayload(
        String orderId,
        String customerId,
        LocalDate orderDate,
        BigDecimal amount,
        String nameOnCard,
        String creditCardNumber,
        String expiry,
        String cvv
) {
}
