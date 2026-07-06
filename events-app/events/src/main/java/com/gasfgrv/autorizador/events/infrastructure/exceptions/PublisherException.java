package com.gasfgrv.autorizador.events.infrastructure.exceptions;

public class PublisherException extends RuntimeException {

    public PublisherException(Exception cause) {
        super("Erro ao publicar evento no Kafka", cause);
    }

}
