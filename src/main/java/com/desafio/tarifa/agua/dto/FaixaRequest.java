package com.desafio.tarifa.agua.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class FaixaRequest {

    @NotNull
    private Integer limiteInferior;

    @NotNull
    private Integer limiteSuperior;

    @NotNull
    @PositiveOrZero
    private BigDecimal valorUnitario;

    public Integer getLimiteInferior() { return limiteInferior; }
    public void setLimiteInferior(Integer limiteInferior) { this.limiteInferior = limiteInferior; }
    public Integer getLimiteSuperior() { return limiteSuperior; }
    public void setLimiteSuperior(Integer limiteSuperior) { this.limiteSuperior = limiteSuperior; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}