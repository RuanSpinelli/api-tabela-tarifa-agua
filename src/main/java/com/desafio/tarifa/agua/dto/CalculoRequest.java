package com.desafio.tarifa.agua.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CalculoRequest {

    @NotNull(message = "ID da tabela é obrigatório")
    private Long tabelaId;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @NotNull(message = "Consumo é obrigatório")
    @Positive(message = "Consumo deve ser positivo")
    private Integer consumo;

    // Getters e Setters
    public Long getTabelaId() { return tabelaId; }
    public void setTabelaId(Long tabelaId) { this.tabelaId = tabelaId; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getConsumo() { return consumo; }
    public void setConsumo(Integer consumo) { this.consumo = consumo; }
}