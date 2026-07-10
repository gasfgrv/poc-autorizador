package com.gasfgrv.autorizador.events.domain.service;

import com.gasfgrv.autorizador.events.domain.exceptions.SemTaskTokenException;

import java.util.Objects;

public class TaskTokenService {

    public void validarTaskToken(String taskToken) {
        if (Objects.isNull(taskToken) || taskToken.isBlank()) {
            throw new SemTaskTokenException();
        }
    }

}
