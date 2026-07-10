package com.gasfgrv.autorizador.events.application.usecases;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.exceptions.SemTaskTokenException;
import com.gasfgrv.autorizador.events.domain.ports.out.EventPublisherPort;
import com.gasfgrv.autorizador.events.domain.service.RespostaExecucaoService;
import com.gasfgrv.autorizador.events.domain.service.TaskTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class AutorizadorUseCaseTest {

    @Mock
    private EventPublisherPort publisher;

    @Mock
    private TaskTokenService taskTokenService;

    @Mock
    private RespostaExecucaoService respostaExecucaoService;

    @InjectMocks
    private AutorizadorUseCase useCase;

    private Pedido pedido;
    private String taskToken;

    @BeforeEach
    void setUp() {
        this.pedido = Pedido.builder()
                .id("1234567")
                .clienteId("98766")
                .dataPedido(LocalDate.of(2024, 1, 14))
                .total(BigDecimal.valueOf(100L))
                .nomeCartao("FIRSTNAME LASTNAME")
                .numeroCartao("1234 1234 1234 1234")
                .expiraEm("XX/YY")
                .cvv("123")
                .build();

        this.taskToken = UUID.randomUUID().toString();
    }

    @Test
    void usecaseDeveEnviarEventoParaAutorizador(CapturedOutput output) {
        doNothing().when(publisher).enviarEvento(pedido, taskToken);

        useCase.autorizarPedido(pedido, taskToken);

        assertTrue(output.getOut().contains("Enviando evento para autorizar pedido"));

        verify(publisher).enviarEvento(pedido, taskToken);
    }

    @Test
    void usecaseDeveLancarExcecaoQuandoTaskTokenForInvalido(CapturedOutput output) {
        doThrow(SemTaskTokenException.class).when(taskTokenService).validarTaskToken(taskToken);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> useCase.enviarResposta(pedido, true, taskToken));

        assertTrue(output.getOut().contains("Enviando resposta para autorizar pedido"));
        assertTrue(output.getOut().contains("Não foi possível enviar evento para autorizar pedido"));

        assertInstanceOf(SemTaskTokenException.class, exception.getCause());

        verify(respostaExecucaoService, never()).responderExecucao(eq(pedido), anyBoolean(), eq(taskToken));
    }

    @Test
    void usecaseDeveEnviarResposta(CapturedOutput output) {
        doNothing().when(taskTokenService).validarTaskToken(taskToken);
        doNothing().when(respostaExecucaoService).responderExecucao(eq(pedido), anyBoolean(), eq(taskToken));

        useCase.enviarResposta(pedido, true, taskToken);

        assertTrue(output.getOut().contains("Enviando resposta para autorizar pedido"));
        assertFalse(output.getOut().contains("Não foi possível enviar evento para autorizar pedido"));

        verify(respostaExecucaoService).responderExecucao(eq(pedido), anyBoolean(), eq(taskToken));
        verify(taskTokenService).validarTaskToken(taskToken);
    }

}
