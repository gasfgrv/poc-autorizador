package com.gasfgrv.autorizador.events.domain.service;

import com.gasfgrv.autorizador.events.domain.exceptions.SemTaskTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskTokenServiceTest {

    private TaskTokenService service;

    @BeforeEach
    void setUp() {
        this.service = new TaskTokenService();
    }

    @Test
    void serviceDeveValidarNormalmenteQuandoTaskTokenNaoForVazio() {
        String taskToken = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> service.validarTaskToken(taskToken));
    }

    @Test
    void serviceDeveLancarExcecaoQuandoTaskTokenForVazio() {
        String taskToken = "";
        assertThrows(SemTaskTokenException.class, () -> service.validarTaskToken(taskToken));
    }

    @Test
    void serviceDeveLancarExcecaoQuandoTaskTokenForEmBranco() {
        String taskToken = "     ";
        assertThrows(SemTaskTokenException.class, () -> service.validarTaskToken(taskToken));
    }

    @Test
    void serviceDeveLancarExcecaoQuandoTaskTokenForNulo() {
        String taskToken = null;
        assertThrows(SemTaskTokenException.class, () -> service.validarTaskToken(taskToken));
    }

}
