package com.gasfgrv.autorizador.events.domain.entities;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class Pedido {

    private String id;
    private String clienteId;
    private LocalDate dataPedido;
    private BigDecimal total;
    private String nomeCartao;
    private String numeroCartao;
    private String expiraEm;
    private String cvv;

}
