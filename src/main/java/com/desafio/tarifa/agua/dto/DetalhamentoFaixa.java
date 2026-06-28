package com.desafio.tarifa.agua.dto;

import java.math.BigDecimal;

public class DetalhamentoFaixa {

    private FaixaInfo faixa;
    private Integer m3Cobrados;
    private BigDecimal valorUnitario;
    private BigDecimal subtotal;

    public DetalhamentoFaixa() {}

    public DetalhamentoFaixa(Integer inicio, Integer fim, Integer m3Cobrados,
                             BigDecimal valorUnitario, BigDecimal subtotal) {
        this.faixa = new FaixaInfo(inicio, fim);
        this.m3Cobrados = m3Cobrados;
        this.valorUnitario = valorUnitario;
        this.subtotal = subtotal;
    }

    // Getters e Setters
    public FaixaInfo getFaixa() { return faixa; }
    public void setFaixa(FaixaInfo faixa) { this.faixa = faixa; }
    public Integer getM3Cobrados() { return m3Cobrados; }
    public void setM3Cobrados(Integer m3Cobrados) { this.m3Cobrados = m3Cobrados; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    // Classe interna
    public static class FaixaInfo {
        private Integer inicio;
        private Integer fim;

        public FaixaInfo() {}

        public FaixaInfo(Integer inicio, Integer fim) {
            this.inicio = inicio;
            this.fim = fim;
        }

        public Integer getInicio() { return inicio; }
        public void setInicio(Integer inicio) { this.inicio = inicio; }
        public Integer getFim() { return fim; }
        public void setFim(Integer fim) { this.fim = fim; }
    }
}