package com.gasfgrv.autorizador.core.domain.entities;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class Resposta {

    private String id;
    private String clienteId;
    private LocalDate dataPedido;
    private BigDecimal total;
    private String nomeCartao;
    private String numeroCartao;
    private String expiraEm;
    private String cvv;
    private boolean aprovado;

    public Resposta aprovarPedido(Pedido pedido, boolean aprovado) {
        this.id = pedido.getId();
        this.clienteId = pedido.getClienteId();
        this.dataPedido = pedido.getDataPedido();
        this.total = pedido.getTotal();
        this.nomeCartao = pedido.getNomeCartao();
        this.numeroCartao = pedido.getNumeroCartao();
        this.expiraEm = pedido.getExpiraEm();
        this.cvv = pedido.getCvv();
        this.aprovado = aprovado;
        return this;
    }

}
