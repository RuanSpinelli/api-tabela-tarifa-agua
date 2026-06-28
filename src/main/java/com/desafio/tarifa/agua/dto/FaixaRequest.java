package com.desafio.tarifa.agua.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class FaixaRequest {

    @NotNull(message = "ID da categoria é obrigatório")
    private Long categoriaId;

    @NotNull(message = "Limite inferior é obrigatório")
    private Integer limiteInferior;

    @NotNull(message = "Limite superior é obrigatório")
    private Integer limiteSuperior;

    @NotNull(message = "Valor unitário é obrigatório")
    @PositiveOrZero(message = "Valor unitário deve ser zero ou positivo")
    private BigDecimal valorUnitario;

    // Getters e Setters
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public Integer getLimiteInferior() { return limiteInferior; }
    public void setLimiteInferior(Integer limiteInferior) { this.limiteInferior = limiteInferior; }
    public Integer getLimiteSuperior() { return limiteSuperior; }
    public void setLimiteSuperior(Integer limiteSuperior) { this.limiteSuperior = limiteSuperior; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}