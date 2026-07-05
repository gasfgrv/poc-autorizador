package com.gasfgrv.autorizador.events.application.usecases;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.ports.in.AutorizadorInputPort;
import com.gasfgrv.autorizador.events.domain.ports.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorizadorUseCase implements AutorizadorInputPort {

    private final EventPublisherPort publisher;

    @Override
    public void autorizarPedido(Pedido pedido) {
        log.info("Enviando evento para autorizar pedido");
        publisher.enviarEvento(pedido);
    }

}
