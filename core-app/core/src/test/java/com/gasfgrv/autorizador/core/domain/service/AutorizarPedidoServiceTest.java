package com.gasfgrv.autorizador.core.domain.service;

import com.gasfgrv.autorizador.core.domain.entities.Pedido;
import com.gasfgrv.autorizador.core.domain.entities.Resposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AutorizarPedidoServiceTest {

    @Mock
    private Random random;

    @InjectMocks
    private AutorizarPedidoService service;

    private Pedido pedido;

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
    }

    @Test
    @DisplayName("Deve responder com um pedido autorizado")
    void deveResponderComUmPedidoAutorizado() {
        // Arrange
        doReturn(Boolean.TRUE).when(random).nextBoolean();

        // Act
        Resposta resposta = service.autorizarPedido(pedido);

        // Assert
        assertSame(pedido.getId(), resposta.getId());
        assertSame(pedido.getClienteId(), resposta.getClienteId());
        assertSame(pedido.getDataPedido(), resposta.getDataPedido());
        assertSame(pedido.getTotal(), resposta.getTotal());
        assertSame(pedido.getNomeCartao(), resposta.getNomeCartao());
        assertSame(pedido.getNumeroCartao(), resposta.getNumeroCartao());
        assertSame(pedido.getExpiraEm(), resposta.getExpiraEm());
        assertSame(pedido.getCvv(), resposta.getCvv());
        assertTrue(resposta.isAprovado());

        verify(random).nextBoolean();
    }

    @Test
    @DisplayName("Deve responder com um pedido não autorizado")
    void deveResponderComUmPedidoNaoAutorizado() {
        // Arrange
        doReturn(Boolean.FALSE).when(random).nextBoolean();

        // Act
        Resposta resposta = service.autorizarPedido(pedido);

        // Assert
        assertSame(pedido.getId(), resposta.getId());
        assertSame(pedido.getClienteId(), resposta.getClienteId());
        assertSame(pedido.getDataPedido(), resposta.getDataPedido());
        assertSame(pedido.getTotal(), resposta.getTotal());
        assertSame(pedido.getNomeCartao(), resposta.getNomeCartao());
        assertSame(pedido.getNumeroCartao(), resposta.getNumeroCartao());
        assertSame(pedido.getExpiraEm(), resposta.getExpiraEm());
        assertSame(pedido.getCvv(), resposta.getCvv());
        assertFalse(resposta.isAprovado());

        verify(random).nextBoolean();
    }

}