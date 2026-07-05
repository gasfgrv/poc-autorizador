package com.gasfgrv.autorizador.events.application.usecases;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.ports.in.AutorizadorInputPort;
import com.gasfgrv.autorizador.events.domain.ports.out.EventPublisherPort;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowRepositoryPort;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowResponsePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorizadorUseCase implements AutorizadorInputPort {

    private final EventPublisherPort publisher;
    private final WorkflowRepositoryPort repository;
    private final WorkflowResponsePort responsePort;

    @Override
    public void autorizarPedido(Pedido pedido, String taskToken) {
        log.info("Enviando evento para autorizar pedido");
        publisher.enviarEvento(pedido, taskToken);
    }

    @Override
    public void enviarResposta(Pedido pedido, boolean approved, String taskToken) {
        if (taskToken.isBlank()) {
            log.error("Não foi possível enviar evento para autorizar pedido");
            return;
        }

        boolean existeExecucao = repository.buscarDadosDaExecucao(taskToken);

        if (!existeExecucao || !approved) {
            log.info("existeExecucao: {}, approved: {}", existeExecucao, approved);
            responsePort.responderComFalha(taskToken);
        }

        log.info("Evento enviado para autorizar pedido");
        responsePort.responderComSucesso(taskToken, pedido);
    }

}
