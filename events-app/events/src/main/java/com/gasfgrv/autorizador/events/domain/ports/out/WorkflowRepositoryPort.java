package com.gasfgrv.autorizador.events.domain.ports.out;

public interface WorkflowRepositoryPort {

    void salvarContextoWorkflow(String taskToken, String executionArn);

    boolean buscarDadosDaExecucao(String taskToken);
}
