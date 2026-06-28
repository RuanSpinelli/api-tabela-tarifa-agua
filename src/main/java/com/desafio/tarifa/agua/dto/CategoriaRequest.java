package com.desafio.tarifa.agua.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CategoriaRequest {

    @NotBlank(message = "Nome da categoria é obrigatório")
    private String categoriaNome;

    @NotEmpty(message = "Deve conter pelo menos uma faixa")
    @Valid
    private List<FaixaRequest> faixas;

    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }
    public List<FaixaRequest> getFaixas() { return faixas; }
    public void setFaixas(List<FaixaRequest> faixas) { this.faixas = faixas; }
}