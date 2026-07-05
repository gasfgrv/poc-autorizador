package com.gasfgrv.autorizador.events.infrastructure.dtos.sqs;

public record WorkflowContextDto(
        String taskToken,
        String executionArn
) { }
