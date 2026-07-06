package com.gasfgrv.autorizador.core.infrastructure.exceptions;

public class ListenerException extends RuntimeException {

    public ListenerException(Exception e) {
        super("Erro ao processar mensagem do tópico", e);
    }

}
