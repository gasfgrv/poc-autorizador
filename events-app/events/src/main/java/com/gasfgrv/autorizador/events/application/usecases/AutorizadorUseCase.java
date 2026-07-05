package com.gasfgrv.autorizador.events.application.usecases;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.ports.in.AutorizadorInputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AutorizadorUseCase implements AutorizadorInputPort {

    @Override
    public void autorizarPedido(Pedido pedido) {
        /*
        recebe o evento
        salva na tabela de controle
        envia para o kafka
         */
    }

}
