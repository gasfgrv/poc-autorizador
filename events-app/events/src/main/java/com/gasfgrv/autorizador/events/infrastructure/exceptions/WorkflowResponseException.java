package com.gasfgrv.autorizador.events.infrastructure.exceptions;

public class WorkflowResponseException extends RuntimeException {

    public WorkflowResponseException(Exception e) {
        super("Erro ao responder workflow", e);
    }

}
