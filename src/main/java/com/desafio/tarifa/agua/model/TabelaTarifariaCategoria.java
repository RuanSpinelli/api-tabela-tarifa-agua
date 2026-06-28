package com.desafio.tarifa.agua.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tabela_tarifaria_id", "categoria_id"})
})
public class TabelaTarifariaCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tabela_tarifaria_id")
    @JsonIgnore
    private TabelaTarifaria tabelaTarifaria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @OneToMany(mappedBy = "tabelaTarifariaCategoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Faixa> faixas = new ArrayList<>();

    public TabelaTarifariaCategoria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TabelaTarifaria getTabelaTarifaria() { return tabelaTarifaria; }
    public void setTabelaTarifaria(TabelaTarifaria tabelaTarifaria) { this.tabelaTarifaria = tabelaTarifaria; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public List<Faixa> getFaixas() { return faixas; }
    public void setFaixas(List<Faixa> faixas) { this.faixas = faixas; }
}