package com.gasfgrv.autorizador.core.application.usecases;

import com.gasfgrv.autorizador.core.domain.entities.Pedido;
import com.gasfgrv.autorizador.core.domain.entities.Resposta;
import com.gasfgrv.autorizador.core.domain.ports.in.AutorizadorResponsePort;
import com.gasfgrv.autorizador.core.domain.ports.out.RespostaSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorizadorUsecase implements AutorizadorResponsePort {

    private final Random random = new Random();

    private final RespostaSenderPort sender;

    @Override
    public void autorizar(Pedido pedido, String taskToken) {
        log.info("Autorizando pedido: {}", pedido);
        Resposta resposta = new Resposta().aprovarPedido(pedido, isAprovado());
        sender.enviarResposta(resposta, taskToken);
    }

    private boolean isAprovado() {
        return this.random.nextBoolean();
    }

}
