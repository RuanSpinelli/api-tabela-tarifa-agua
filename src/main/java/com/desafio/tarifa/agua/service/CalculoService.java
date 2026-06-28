package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.CalculoRequest;
import com.desafio.tarifa.agua.dto.CalculoResponse;
import com.desafio.tarifa.agua.dto.DetalhamentoFaixa;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import com.desafio.tarifa.agua.model.Categoria;
import com.desafio.tarifa.agua.model.Faixa;
import com.desafio.tarifa.agua.repository.CategoriaRepository;
import com.desafio.tarifa.agua.repository.FaixaRepository;
import com.desafio.tarifa.agua.repository.TabelaTarifariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoService {

    @Autowired
    private FaixaRepository faixaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TabelaTarifariaRepository tabelaRepository;

    public CalculoResponse calcular(CalculoRequest request) {
        if (!tabelaRepository.existsById(request.getTabelaId())) {
            throw new RegraNegocioException("Tabela tarifaria nao encontrada: " + request.getTabelaId());
        }

        Categoria categoria = categoriaRepository.findByNome(request.getCategoria().toUpperCase())
                .orElseThrow(() -> new RegraNegocioException("Categoria nao encontrada: " + request.getCategoria()));

        List<Faixa> faixas = faixaRepository
                .findByTabelaTarifariaIdAndCategoriaIdOrderByLimiteInferiorAsc(
                        request.getTabelaId(), categoria.getId());

        if (faixas.isEmpty()) {
            throw new RegraNegocioException(
                    "Nenhuma faixa encontrada para a categoria " + request.getCategoria() +
                            " na tabela " + request.getTabelaId());
        }

        CalculoResponse response = new CalculoResponse();
        response.setCategoria(categoria.getNome());
        response.setConsumoTotal(request.getConsumo());

        BigDecimal valorTotal = BigDecimal.ZERO;
        List<DetalhamentoFaixa> detalhamento = new ArrayList<>();
        int restante = request.getConsumo();

        for (Faixa faixa : faixas) {
            if (restante <= 0) break;

            int tamanhoFaixa = faixa.getLimiteSuperior() - faixa.getLimiteInferior() + 1;
            int m3Cobrados = Math.min(restante, tamanhoFaixa);

            if (m3Cobrados > 0 && faixa.getLimiteInferior() == 0) {
                m3Cobrados = Math.min(restante, tamanhoFaixa - 1);
            }

            DetalhamentoFaixa det = new DetalhamentoFaixa(
                    faixa.getLimiteInferior(),
                    faixa.getLimiteSuperior(),
                    m3Cobrados,
                    faixa.getValorUnitario(),
                    faixa.getValorUnitario().multiply(BigDecimal.valueOf(m3Cobrados))
            );

            detalhamento.add(det);
            valorTotal = valorTotal.add(det.getSubtotal());
            restante -= m3Cobrados;
        }

        if (restante > 0) {
            throw new RegraNegocioException(
                    "Consumo de " + request.getConsumo() + " m3 excede a cobertura das faixas cadastradas");
        }

        response.setValorTotal(valorTotal);
        response.setDetalhamento(detalhamento);
        return response;
    }
}