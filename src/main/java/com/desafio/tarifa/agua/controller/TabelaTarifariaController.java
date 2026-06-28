package com.desafio.tarifa.agua.controller;

import com.desafio.tarifa.agua.dto.TabelaTarifariaRequest;
import com.desafio.tarifa.agua.model.TabelaTarifaria;
import com.desafio.tarifa.agua.service.TabelaTarifariaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tabelas-tarifarias")
public class TabelaTarifariaController {

    @Autowired
    private TabelaTarifariaService service;

    @PostMapping
    public ResponseEntity<TabelaTarifaria> criar(@RequestBody @Valid TabelaTarifariaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<TabelaTarifaria>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(Map.of("mensagem", "Tabela tarifaria excluida com sucesso"));
    }
}