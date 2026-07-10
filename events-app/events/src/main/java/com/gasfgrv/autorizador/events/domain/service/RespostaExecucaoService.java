package com.gasfgrv.autorizador.events.domain.service;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowRepositoryPort;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowResponsePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RespostaExecucaoService {

    private final WorkflowRepositoryPort repository;
    private final WorkflowResponsePort responsePort;

    public void responderExecucao(Pedido pedido, boolean approved, String taskToken) {
        boolean existeExecucao = repository.buscarDadosDaExecucao(taskToken);

        if (!existeExecucao || !approved) {
            responsePort.responderComFalha(taskToken);
        }

        responsePort.responderComSucesso(taskToken, pedido);
    }

}
