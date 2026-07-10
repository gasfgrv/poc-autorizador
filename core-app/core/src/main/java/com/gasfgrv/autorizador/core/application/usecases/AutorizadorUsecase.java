package com.gasfgrv.autorizador.core.application.usecases;

import com.gasfgrv.autorizador.core.domain.entities.Pedido;
import com.gasfgrv.autorizador.core.domain.entities.Resposta;
import com.gasfgrv.autorizador.core.domain.ports.in.AutorizadorResponsePort;
import com.gasfgrv.autorizador.core.domain.ports.out.RespostaSenderPort;
import com.gasfgrv.autorizador.core.domain.service.AutorizarPedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorizadorUsecase implements AutorizadorResponsePort {

    private final RespostaSenderPort sender;
    private final AutorizarPedidoService service;

    @Override
    public void autorizar(Pedido pedido, String taskToken) {
        log.info("Autorizando pedido: {}", pedido);
        Resposta resposta = service.autorizarPedido(pedido);
        sender.enviarResposta(resposta, taskToken);
    }

}
