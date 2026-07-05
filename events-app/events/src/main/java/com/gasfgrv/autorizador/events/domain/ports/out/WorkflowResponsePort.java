package com.gasfgrv.autorizador.events.domain.ports.out;

public interface WorkflowResponsePort {

    void responderComSucesso();

    void responderComFalha();

}
