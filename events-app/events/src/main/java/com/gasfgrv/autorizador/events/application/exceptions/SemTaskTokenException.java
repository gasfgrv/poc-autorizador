package com.gasfgrv.autorizador.events.application.exceptions;

public class SemTaskTokenException extends RuntimeException {

    public SemTaskTokenException() {
        super("Não foi possível enviar evento para autorizar pedido, pois não foi fornecido o taskToken");
    }

}
