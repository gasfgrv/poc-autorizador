package com.gasfgrv.autorizador.events.domain.ports.in;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;

public interface AutorizadorInputPort {

    void autorizarPedido(Pedido pedido);

}
