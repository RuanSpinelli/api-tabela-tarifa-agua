package com.desafio.tarifa.agua.dto;

import java.math.BigDecimal;
import java.util.List;

public class CalculoResponse {

    private String categoria;
    private Integer consumoTotal;
    private BigDecimal valorTotal;
    private List<DetalhamentoFaixa> detalhamento;

    // Getters e Setters
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getConsumoTotal() { return consumoTotal; }
    public void setConsumoTotal(Integer consumoTotal) { this.consumoTotal = consumoTotal; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public List<DetalhamentoFaixa> getDetalhamento() { return detalhamento; }
    public void setDetalhamento(List<DetalhamentoFaixa> detalhamento) { this.detalhamento = detalhamento; }
}