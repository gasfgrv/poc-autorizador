package com.gasfgrv.autorizador.events.domain.ports.out;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;

public interface EventPublisherPort {

    void enviarEvento(Pedido pedido);

}
