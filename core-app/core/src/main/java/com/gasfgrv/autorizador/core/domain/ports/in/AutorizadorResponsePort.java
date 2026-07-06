package com.gasfgrv.autorizador.core.domain.ports.in;

import com.gasfgrv.autorizador.core.domain.entities.Pedido;

public interface AutorizadorResponsePort {

    void autorizar(Pedido domain, String taskToken);

}
