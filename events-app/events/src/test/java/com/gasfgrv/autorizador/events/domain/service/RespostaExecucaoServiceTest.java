package com.gasfgrv.autorizador.events.domain.service;

import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowRepositoryPort;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowResponsePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RespostaExecucaoServiceTest {

    @Captor
    private ArgumentCaptor<Pedido> captor;

    @Mock
    private WorkflowRepositoryPort repository;

    @Mock
    private WorkflowResponsePort response;

    @InjectMocks
    private RespostaExecucaoService service;

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
    void serviceDeveResponderComFalhaQuandoNaoExisteExecucao() {
        doReturn(false).when(repository).buscarDadosDaExecucao(taskToken);
        doNothing().when(response).responderComFalha(taskToken);

        service.responderExecucao(pedido, true, taskToken);

        verify(repository).buscarDadosDaExecucao(taskToken);
        verify(response, never()).responderComSucesso(anyString(), any(Pedido.class));
        verify(response).responderComFalha(taskToken);
    }

    @Test
    void serviceDeveResponderComFalhaQuandoNaoAprovado() {
        doReturn(true).when(repository).buscarDadosDaExecucao(taskToken);
        doNothing().when(response).responderComFalha(taskToken);

        service.responderExecucao(pedido, false, taskToken);

        verify(repository).buscarDadosDaExecucao(taskToken);
        verify(response, never()).responderComSucesso(anyString(), any(Pedido.class));
        verify(response).responderComFalha(taskToken);
    }

    @Test
    void serviceDeveResponderComSucesso() {
        doReturn(true).when(repository).buscarDadosDaExecucao(taskToken);
        doNothing().when(response).responderComSucesso(eq(taskToken), captor.capture());

        service.responderExecucao(pedido, true, taskToken);

        Pedido value = captor.getValue();
        assertSame(pedido, value);

        verify(repository).buscarDadosDaExecucao(taskToken);
        verify(response).responderComSucesso(taskToken, value);
        verify(response, never()).responderComFalha(anyString());
    }

}
