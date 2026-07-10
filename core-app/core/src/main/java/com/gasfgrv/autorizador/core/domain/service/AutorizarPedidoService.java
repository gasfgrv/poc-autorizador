package com.gasfgrv.autorizador.core.domain.service;

import com.gasfgrv.autorizador.core.domain.entities.Pedido;
import com.gasfgrv.autorizador.core.domain.entities.Resposta;
import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public class AutorizarPedidoService {

    private final Random random;

    public Resposta autorizarPedido(Pedido pedido) {
        Resposta resposta = new Resposta();
        resposta.aprovarPedido(pedido, isAprovado());
        return resposta;
    }

    private boolean isAprovado() {
        return this.random.nextBoolean();
    }

}
