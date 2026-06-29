package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.model.Categoria;
import com.desafio.tarifa.agua.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listarTodas() {
        return repository.findAll();
    }
}