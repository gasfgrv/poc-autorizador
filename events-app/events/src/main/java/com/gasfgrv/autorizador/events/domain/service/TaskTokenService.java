package com.gasfgrv.autorizador.events.domain.service;

import com.gasfgrv.autorizador.events.domain.exceptions.SemTaskTokenException;

public class TaskTokenService {

    public void validarTaskToken(String taskToken) {
        if (!taskToken.isBlank()) {
            return;
        }

        throw new SemTaskTokenException();
    }

}
