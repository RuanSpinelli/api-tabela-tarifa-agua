package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.FaixaRequest;
import com.desafio.tarifa.agua.dto.TabelaTarifariaRequest;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import com.desafio.tarifa.agua.model.Categoria;
import com.desafio.tarifa.agua.model.Faixa;
import com.desafio.tarifa.agua.model.TabelaTarifaria;
import com.desafio.tarifa.agua.repository.CategoriaRepository;
import com.desafio.tarifa.agua.repository.FaixaRepository;
import com.desafio.tarifa.agua.repository.TabelaTarifariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TabelaTarifariaService {

    @Autowired
    private TabelaTarifariaRepository tabelaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional
    public TabelaTarifaria criar(TabelaTarifariaRequest request) {
        TabelaTarifaria tabela = new TabelaTarifaria();
        tabela.setNome(request.getNome());
        tabela.setVigenciaInicio(request.getVigenciaInicio());
        tabela.setVigenciaFim(request.getVigenciaFim());

        Map<Long, List<FaixaRequest>> faixasPorCategoria = new LinkedHashMap<>();
        for (FaixaRequest fr : request.getFaixas()) {
            faixasPorCategoria.computeIfAbsent(fr.getCategoriaId(), k -> new ArrayList<>()).add(fr);
        }

        List<Faixa> faixasEntidade = new ArrayList<>();

        for (Map.Entry<Long, List<FaixaRequest>> entry : faixasPorCategoria.entrySet()) {
            Long categoriaId = entry.getKey();
            List<FaixaRequest> faixasDaCategoria = entry.getValue();

            faixasDaCategoria.sort(Comparator.comparingInt(FaixaRequest::getLimiteInferior));
            validarEscada(faixasDaCategoria, categoriaId);

            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new RegraNegocioException("Categoria " + categoriaId + " nao encontrada"));

            for (FaixaRequest fr : faixasDaCategoria) {
                Faixa faixa = new Faixa();
                faixa.setTabelaTarifaria(tabela);
                faixa.setCategoria(categoria);
                faixa.setLimiteInferior(fr.getLimiteInferior());
                faixa.setLimiteSuperior(fr.getLimiteSuperior());
                faixa.setValorUnitario(fr.getValorUnitario());
                faixasEntidade.add(faixa);
            }
        }

        tabela.setFaixas(faixasEntidade);
        return tabelaRepository.save(tabela);
    }

    public List<TabelaTarifaria> listarTodas() {
        return tabelaRepository.findAll();
    }

    @Transactional
    public void excluir(Long id) {
        if (!tabelaRepository.existsById(id)) {
            throw new RegraNegocioException("Tabela tarifaria nao encontrada: " + id);
        }
        tabelaRepository.deleteById(id);
    }

    private void validarEscada(List<FaixaRequest> faixas, Long categoriaId) {
        if (faixas.isEmpty()) return;

        if (faixas.get(0).getLimiteInferior() != 0) {
            throw new RegraNegocioException(
                    "Categoria " + categoriaId + ": primeira faixa deve comecar em 0");
        }

        for (int i = 0; i < faixas.size(); i++) {
            FaixaRequest atual = faixas.get(i);

            if (atual.getLimiteInferior() >= atual.getLimiteSuperior()) {
                throw new RegraNegocioException(
                        "Categoria " + categoriaId + ": limite inferior (" + atual.getLimiteInferior() +
                                ") deve ser menor que o superior (" + atual.getLimiteSuperior() + ")");
            }

            if (i < faixas.size() - 1) {
                FaixaRequest proxima = faixas.get(i + 1);
                if (proxima.getLimiteInferior() != atual.getLimiteSuperior() + 1) {
                    throw new RegraNegocioException(
                            "Categoria " + categoriaId + ": buraco entre faixas. " +
                                    "Esperado inicio em " + (atual.getLimiteSuperior() + 1) +
                                    ", mas recebeu " + proxima.getLimiteInferior());
                }
            }
        }

        FaixaRequest ultima = faixas.get(faixas.size() - 1);
        if (ultima.getLimiteSuperior() < 99999) {
            throw new RegraNegocioException(
                    "Categoria " + categoriaId + ": ultima faixa deve ter limite superior >= 99999");
        }
    }
}