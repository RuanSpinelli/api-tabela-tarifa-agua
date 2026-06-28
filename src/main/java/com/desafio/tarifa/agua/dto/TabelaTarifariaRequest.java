package com.desafio.tarifa.agua.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

public class TabelaTarifariaRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFim;

    @NotEmpty(message = "Deve conter pelo menos uma faixa")
    @Valid
    private List<FaixaRequest> faixas;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getVigenciaInicio() { return vigenciaInicio; }
    public void setVigenciaInicio(LocalDate vigenciaInicio) { this.vigenciaInicio = vigenciaInicio; }
    public LocalDate getVigenciaFim() { return vigenciaFim; }
    public void setVigenciaFim(LocalDate vigenciaFim) { this.vigenciaFim = vigenciaFim; }
    public List<FaixaRequest> getFaixas() { return faixas; }
    public void setFaixas(List<FaixaRequest> faixas) { this.faixas = faixas; }
}