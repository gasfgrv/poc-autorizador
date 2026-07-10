package com.gasfgrv.autorizador.core.application.usecases;

import com.gasfgrv.autorizador.core.domain.entities.Pedido;
import com.gasfgrv.autorizador.core.domain.entities.Resposta;
import com.gasfgrv.autorizador.core.domain.ports.out.RespostaSenderPort;
import com.gasfgrv.autorizador.core.domain.service.AutorizarPedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutorizadorUsecaseTest {

    @Captor
    private ArgumentCaptor<Resposta> respostaCaptor;

    @Mock
    private RespostaSenderPort sender;

    @Mock
    private AutorizarPedidoService service;

    @InjectMocks
    private AutorizadorUsecase usecase;

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
    @DisplayName("usecase deve processar um pedido aprovado")
    void usecaseDeveProcessarUmPedidoAprovado() {
        // Arrange
        doReturn(gerarResposta(pedido, true)).when(service).autorizarPedido(pedido);
        doNothing().when(sender).enviarResposta(respostaCaptor.capture(), eq(taskToken));

        // Act
        usecase.autorizar(pedido, taskToken);

        // Assert
        Resposta resposta = respostaCaptor.getValue();
        assertSame(pedido.getId(), resposta.getId());
        assertSame(pedido.getClienteId(), resposta.getClienteId());
        assertSame(pedido.getDataPedido(), resposta.getDataPedido());
        assertSame(pedido.getTotal(), resposta.getTotal());
        assertSame(pedido.getNomeCartao(), resposta.getNomeCartao());
        assertSame(pedido.getNumeroCartao(), resposta.getNumeroCartao());
        assertSame(pedido.getExpiraEm(), resposta.getExpiraEm());
        assertSame(pedido.getCvv(), resposta.getCvv());
        assertTrue(resposta.isAprovado());

        verify(service).autorizarPedido(pedido);
        verify(sender).enviarResposta(resposta, taskToken);
    }

    @Test
    @DisplayName("usecase deve processar um pedido não aprovado")
    void usecaseDeveProcessarUmPedidoNaoAprovado() {
        // Arrange
        doReturn(gerarResposta(pedido, false)).when(service).autorizarPedido(pedido);
        doNothing().when(sender).enviarResposta(respostaCaptor.capture(), eq(taskToken));

        // Act
        usecase.autorizar(pedido, taskToken);

        // Assert
        Resposta resposta = respostaCaptor.getValue();
        assertSame(pedido.getId(), resposta.getId());
        assertSame(pedido.getClienteId(), resposta.getClienteId());
        assertSame(pedido.getDataPedido(), resposta.getDataPedido());
        assertSame(pedido.getTotal(), resposta.getTotal());
        assertSame(pedido.getNomeCartao(), resposta.getNomeCartao());
        assertSame(pedido.getNumeroCartao(), resposta.getNumeroCartao());
        assertSame(pedido.getExpiraEm(), resposta.getExpiraEm());
        assertSame(pedido.getCvv(), resposta.getCvv());
        assertFalse(resposta.isAprovado());

        verify(service).autorizarPedido(pedido);
        verify(sender).enviarResposta(resposta, taskToken);
    }

    private Resposta gerarResposta(Pedido pedido, boolean isAprovado) {
        return Resposta.builder()
                .id(pedido.getId())
                .clienteId(pedido.getClienteId())
                .dataPedido(pedido.getDataPedido())
                .total(pedido.getTotal())
                .nomeCartao(pedido.getNomeCartao())
                .numeroCartao(pedido.getNumeroCartao())
                .expiraEm(pedido.getExpiraEm())
                .cvv(pedido.getCvv())
                .aprovado(isAprovado)
                .build();
    }

}