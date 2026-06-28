package com.desafio.tarifa.agua.controller;

import com.desafio.tarifa.agua.dto.CalculoRequest;
import com.desafio.tarifa.agua.dto.CalculoResponse;
import com.desafio.tarifa.agua.service.CalculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculos")
public class CalculoController {

    @Autowired
    private CalculoService service;

    @PostMapping
    public ResponseEntity<CalculoResponse> calcular(@RequestBody @Valid CalculoRequest request) {
        return ResponseEntity.ok(service.calcular(request));
    }
}