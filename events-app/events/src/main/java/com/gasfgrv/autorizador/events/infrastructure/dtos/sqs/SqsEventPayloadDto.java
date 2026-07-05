package com.gasfgrv.autorizador.events.infrastructure.dtos.sqs;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SqsEventPayloadDto(
        String orderId,
        String customerId,
        LocalDate orderDate,
        BigDecimal amount,
        String nameOnCard,
        String creditCardNumber,
        String expiry,
        String cvv,
        String taskToken,
        String executionArn
) {

    public EventDetailsDto toPaymentDetails() {
        return new EventDetailsDto(orderId, customerId, orderDate, amount, nameOnCard, creditCardNumber, expiry, cvv);
    }

    public WorkflowContextDto toWorkflowContext() {
        return new WorkflowContextDto(taskToken, executionArn);
    }

}
