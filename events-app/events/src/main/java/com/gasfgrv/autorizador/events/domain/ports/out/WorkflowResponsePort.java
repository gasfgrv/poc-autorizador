package com.gasfgrv.autorizador.events.domain.ports.out;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;

public interface WorkflowResponsePort {

    void responderComSucesso(String taskToken, Pedido response);

    void responderComFalha(String taskToken);

}
