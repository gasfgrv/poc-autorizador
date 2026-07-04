package com.gasfgrv.autorizador.events.infrastructure.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderPayloadDto(
        String orderId,
        String customerId,
        LocalDate orderDate,
        BigDecimal amount,
        String nameOnCard,
        String creditCardNumber,
        String expiry,
        String cvv
) { }
