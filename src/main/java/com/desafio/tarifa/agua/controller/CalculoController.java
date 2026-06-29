package com.desafio.tarifa.agua.controller;

import com.desafio.tarifa.agua.dto.CalculoRequest;
import com.desafio.tarifa.agua.dto.CalculoResponse;
import com.desafio.tarifa.agua.service.CalculoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculos")
public class CalculoController {

    private final CalculoService service;

    public CalculoController(CalculoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Calcular valor a pagar",
            description = "Realiza o cálculo progressivo por faixas de consumo. Retorna o valor total e o detalhamento por faixa.")
    public ResponseEntity<CalculoResponse> calcular(@RequestBody @Valid CalculoRequest request) {
        return ResponseEntity.ok(service.calcular(request));
    }
}