package com.gasfgrv.autorizador.events.infrastructure.exceptions;

public class AutorizadorListenerException extends RuntimeException {

    public AutorizadorListenerException(Exception e) {
        super(e);
    }

}
