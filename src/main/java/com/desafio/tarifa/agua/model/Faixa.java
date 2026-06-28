package com.desafio.tarifa.agua.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tabela_tarifaria_id", "categoria_id", "limiteInferior"})
})
public class Faixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tabela_tarifaria_id")
    @JsonBackReference
    private TabelaTarifaria tabelaTarifaria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    @JsonIgnoreProperties({"faixas"})
    private Categoria categoria;

    @Column(nullable = false)
    private Integer limiteInferior;

    @Column(nullable = false)
    private Integer limiteSuperior;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    public Faixa() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TabelaTarifaria getTabelaTarifaria() { return tabelaTarifaria; }
    public void setTabelaTarifaria(TabelaTarifaria tabelaTarifaria) { this.tabelaTarifaria = tabelaTarifaria; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Integer getLimiteInferior() { return limiteInferior; }
    public void setLimiteInferior(Integer limiteInferior) { this.limiteInferior = limiteInferior; }
    public Integer getLimiteSuperior() { return limiteSuperior; }
    public void setLimiteSuperior(Integer limiteSuperior) { this.limiteSuperior = limiteSuperior; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}