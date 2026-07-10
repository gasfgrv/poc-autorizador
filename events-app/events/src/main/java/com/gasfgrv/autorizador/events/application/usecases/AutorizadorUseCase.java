package com.gasfgrv.autorizador.events.application.usecases;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.exceptions.SemTaskTokenException;
import com.gasfgrv.autorizador.events.domain.ports.in.AutorizadorInputPort;
import com.gasfgrv.autorizador.events.domain.ports.out.EventPublisherPort;
import com.gasfgrv.autorizador.events.domain.service.RespostaExecucaoService;
import com.gasfgrv.autorizador.events.domain.service.TaskTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorizadorUseCase implements AutorizadorInputPort {

    private final EventPublisherPort publisher;
    private final TaskTokenService taskTokenService;
    private final RespostaExecucaoService respostaExecucaoService;

    @Override
    public void autorizarPedido(Pedido pedido, String taskToken) {
        log.info("Enviando evento para autorizar pedido");
        publisher.enviarEvento(pedido, taskToken);
    }

    @Override
    public void enviarResposta(Pedido pedido, boolean approved, String taskToken) {
        try {
            log.info("Enviando resposta para autorizar pedido");
            taskTokenService.validarTaskToken(taskToken);
            respostaExecucaoService.responderExecucao(pedido, approved, taskToken);
        } catch (SemTaskTokenException e) {
            log.error("Não foi possível enviar evento para autorizar pedido");
            throw new RuntimeException(e);
        }
    }

}
