package com.gasfgrv.autorizador.events.infrastructure.dtos.sqs;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EventDetailsDto(
        String orderId,
        String customerId,
        LocalDate orderDate,
        BigDecimal amount,
        String nameOnCard,
        String creditCardNumber,
        String expiry,
        String cvv
) { }
