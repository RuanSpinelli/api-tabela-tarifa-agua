package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.FaixaRequest;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidacaoFaixaService {

    /**
     * Valida as 4 regras de consistência do desafio.
     */
    public void validar(List<FaixaRequest> faixas, String categoriaNome) {
        if (faixas.isEmpty()) return;

        validarCoberturaCompleta(faixas, categoriaNome);

        for (int i = 0; i < faixas.size(); i++) {
            FaixaRequest atual = faixas.get(i);
            validarOrdemValida(atual, categoriaNome);

            if (i < faixas.size() - 1) {
                FaixaRequest proxima = faixas.get(i + 1);
                validarOrdemValida(proxima, categoriaNome);
                validarNaoSobreposicao(atual, proxima, categoriaNome);
            }
        }

        validarCoberturaSuficiente(faixas, categoriaNome);
    }

    private void validarCoberturaCompleta(List<FaixaRequest> faixas, String categoriaNome) {
        if (faixas.get(0).getLimiteInferior() != 0) {
            throw new RegraNegocioException(
                    "Categoria " + categoriaNome + ": primeira faixa deve comecar em 0");
        }
    }

    private void validarOrdemValida(FaixaRequest faixa, String categoriaNome) {
        if (faixa.getLimiteInferior() >= faixa.getLimiteSuperior()) {
            throw new RegraNegocioException(
                    "Categoria " + categoriaNome + ": limite inferior (" + faixa.getLimiteInferior() +
                            ") deve ser menor que o superior (" + faixa.getLimiteSuperior() + ")");
        }
    }

    private void validarNaoSobreposicao(FaixaRequest atual, FaixaRequest proxima, String categoriaNome) {
        if (proxima.getLimiteInferior() != atual.getLimiteSuperior() + 1) {
            throw new RegraNegocioException(
                    "Categoria " + categoriaNome + ": buraco entre faixas. " +
                            "Esperado inicio em " + (atual.getLimiteSuperior() + 1) +
                            ", mas recebeu " + proxima.getLimiteInferior());
        }
    }

    private void validarCoberturaSuficiente(List<FaixaRequest> faixas, String categoriaNome) {
        FaixaRequest ultima = faixas.get(faixas.size() - 1);
        if (ultima.getLimiteSuperior() < 99999) {
            throw new RegraNegocioException(
                    "Categoria " + categoriaNome + ": ultima faixa deve ter limite superior >= 99999");
        }
    }
}