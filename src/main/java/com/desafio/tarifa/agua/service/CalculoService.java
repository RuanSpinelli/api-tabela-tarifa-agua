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

    /**
     * Realiza o cálculo progressivo do valor a pagar com base no consumo em m³.
     * O cálculo é feito por faixas: cada faixa cobra uma parte do consumo,
     * do menor para o maior intervalo, multiplicando os m³ pelo valor unitário.
     *
     * Exemplo: 18 m³ na categoria INDUSTRIAL
     *   Faixa 1 (0-10): 10 m³ × R$1,00 = R$10,00
     *   Faixa 2 (11-20): 8 m³ × R$2,00 = R$16,00
     *   Total: R$26,00
     */
    public CalculoResponse calcular(CalculoRequest request) {
        // Verifica se a tabela tarifária informada existe
        if (!tabelaRepository.existsById(request.getTabelaId())) {
            throw new RegraNegocioException("Tabela tarifaria nao encontrada: " + request.getTabelaId());
        }

        // Busca a categoria pelo nome (case insensitive)
        Categoria categoria = categoriaRepository.findByNome(request.getCategoria().toUpperCase())
                .orElseThrow(() -> new RegraNegocioException("Categoria nao encontrada: " + request.getCategoria()));

        // Busca as faixas ordenadas por limite inferior (crescente)
        List<Faixa> faixas = faixaRepository
                .findByTabelaTarifariaIdAndCategoriaIdOrderByLimiteInferiorAsc(
                        request.getTabelaId(), categoria.getId());

        if (faixas.isEmpty()) {
            throw new RegraNegocioException(
                    "Nenhuma faixa encontrada para a categoria " + request.getCategoria() +
                            " na tabela " + request.getTabelaId());
        }

        // Prepara a resposta
        CalculoResponse response = new CalculoResponse();
        response.setCategoria(categoria.getNome());
        response.setConsumoTotal(request.getConsumo());

        BigDecimal valorTotal = BigDecimal.ZERO;
        List<DetalhamentoFaixa> detalhamento = new ArrayList<>();
        int restante = request.getConsumo();

        // Cálculo progressivo: percorre cada faixa e desconta o consumo
        for (Faixa faixa : faixas) {
            if (restante <= 0) break;

            // Tamanho da faixa considerando intervalo fechado (ex: 0-10 = 11 posições)
            int tamanhoFaixa = faixa.getLimiteSuperior() - faixa.getLimiteInferior() + 1;
            int m3Cobrados = Math.min(restante, tamanhoFaixa);

            // Ajuste: a faixa 0-10 tem 11 posições, mas 0 m³ não é cobrado
            // Portanto, cobra no máximo 10 m³ nesta faixa
            if (m3Cobrados > 0 && faixa.getLimiteInferior() == 0) {
                m3Cobrados = Math.min(restante, tamanhoFaixa - 1);
            }

            // Monta o detalhamento desta faixa com início, fim, m³ cobrados e subtotal
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

        // Se ainda sobrou consumo, as faixas não cobrem o valor informado
        if (restante > 0) {
            throw new RegraNegocioException(
                    "Consumo de " + request.getConsumo() + " m3 excede a cobertura das faixas cadastradas");
        }

        response.setValorTotal(valorTotal);
        response.setDetalhamento(detalhamento);
        return response;
    }
}