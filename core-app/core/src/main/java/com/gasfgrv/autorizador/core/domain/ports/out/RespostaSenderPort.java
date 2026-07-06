package com.gasfgrv.autorizador.core.domain.ports.out;

import com.gasfgrv.autorizador.core.domain.entities.Resposta;

public interface RespostaSenderPort {

    void enviarResposta(Resposta resposta, String taskToken);

}
