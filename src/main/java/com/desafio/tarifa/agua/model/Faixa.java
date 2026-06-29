package com.desafio.tarifa.agua.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tabela_tarifaria_categoria_id", "limiteInferior"})
})
public class Faixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tabela_tarifaria_categoria_id")
    @JsonIgnore
    private TabelaTarifariaCategoria tabelaTarifariaCategoria;

    @Column(nullable = false)
    private Integer limiteInferior;

    @Column(nullable = false)
    private Integer limiteSuperior;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    public Faixa() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TabelaTarifariaCategoria getTabelaTarifariaCategoria() { return tabelaTarifariaCategoria; }
    public void setTabelaTarifariaCategoria(TabelaTarifariaCategoria tabelaTarifariaCategoria) { this.tabelaTarifariaCategoria = tabelaTarifariaCategoria; }
    public Integer getLimiteInferior() { return limiteInferior; }
    public void setLimiteInferior(Integer limiteInferior) { this.limiteInferior = limiteInferior; }
    public Integer getLimiteSuperior() { return limiteSuperior; }
    public void setLimiteSuperior(Integer limiteSuperior) { this.limiteSuperior = limiteSuperior; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}