package com.gasfgrv.autorizador.events.domain.ports.in;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;

public interface AutorizadorInputPort {

    void autorizarPedido(Pedido pedido, String s);

    void enviarResposta(Pedido pedido, boolean approved, String taskToken);
}
