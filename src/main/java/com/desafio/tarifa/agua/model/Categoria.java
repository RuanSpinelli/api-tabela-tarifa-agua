package com.desafio.tarifa.agua.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<TabelaTarifariaCategoria> tabelaCategorias = new ArrayList<>();

    public Categoria() {}

    public Categoria(String nome) {
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<TabelaTarifariaCategoria> getTabelaCategorias() { return tabelaCategorias; }
    public void setTabelaCategorias(List<TabelaTarifariaCategoria> tabelaCategorias) { this.tabelaCategorias = tabelaCategorias; }
}