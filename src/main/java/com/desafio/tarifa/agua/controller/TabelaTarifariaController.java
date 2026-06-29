package com.desafio.tarifa.agua.controller;

import com.desafio.tarifa.agua.dto.TabelaTarifariaRequest;
import com.desafio.tarifa.agua.model.TabelaTarifaria;
import com.desafio.tarifa.agua.service.TabelaTarifariaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tabelas-tarifarias")
public class TabelaTarifariaController {

    private final TabelaTarifariaService service;

    public TabelaTarifariaController(TabelaTarifariaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar tabela tarifária", description = "Cria uma nova tabela com categorias e faixas em lote")
    public ResponseEntity<TabelaTarifaria> criar(@RequestBody @Valid TabelaTarifariaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar tabelas tarifárias", description = "Retorna todas as tabelas cadastradas com suas categorias e faixas")
    public ResponseEntity<List<TabelaTarifaria>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tabela tarifária",
            description = "ATENÇÃO: Substitui integralmente categorias e faixas. O que não for enviado será removido.")
    public ResponseEntity<TabelaTarifaria> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TabelaTarifariaRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir tabela tarifária", description = "Remove a tabela e todas as categorias/faixas vinculadas")
    public ResponseEntity<Map<String, String>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(Map.of("mensagem", "Tabela tarifaria excluida com sucesso"));
    }
}