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

    /**
     * Cria uma nova tabela tarifária com faixas organizadas por categoria.
     * O nome da categoria é usado como chave de agrupamento.
     */
    @Transactional
    public TabelaTarifaria criar(TabelaTarifariaRequest request) {
        TabelaTarifaria tabela = new TabelaTarifaria();
        tabela.setNome(request.getNome());
        tabela.setVigenciaInicio(request.getVigenciaInicio());
        tabela.setVigenciaFim(request.getVigenciaFim());

        // Agrupa faixas por nome da categoria (ex: "COMERCIAL", "INDUSTRIAL")
        Map<String, List<FaixaRequest>> faixasPorCategoria = new LinkedHashMap<>();
        for (FaixaRequest fr : request.getFaixas()) {
            String nomeCategoria = fr.getCategoriaNome().toUpperCase();
            faixasPorCategoria.computeIfAbsent(nomeCategoria, k -> new ArrayList<>()).add(fr);
        }

        List<Faixa> faixasEntidade = new ArrayList<>();

        for (Map.Entry<String, List<FaixaRequest>> entry : faixasPorCategoria.entrySet()) {
            String categoriaNome = entry.getKey();
            List<FaixaRequest> faixasDaCategoria = entry.getValue();

            // Ordena por limite inferior antes de validar
            faixasDaCategoria.sort(Comparator.comparingInt(FaixaRequest::getLimiteInferior));
            validarEscada(faixasDaCategoria, categoriaNome);

            // Busca a categoria pelo nome no banco
            Categoria categoria = categoriaRepository.findByNome(categoriaNome)
                    .orElseThrow(() -> new RegraNegocioException("Categoria " + categoriaNome + " nao encontrada"));

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

    /**
     * Lista todas as tabelas tarifárias cadastradas.
     */
    public List<TabelaTarifaria> listarTodas() {
        return tabelaRepository.findAll();
    }

    /**
     * Atualiza uma tabela tarifária existente mantendo o mesmo ID.
     * Remove as faixas antigas e insere as novas em uma única transação.
     */
    @Transactional
    public TabelaTarifaria atualizar(Long id, TabelaTarifariaRequest request) {
        TabelaTarifaria tabela = tabelaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Tabela tarifaria nao encontrada: " + id));

        // Atualiza dados básicos
        tabela.setNome(request.getNome());
        tabela.setVigenciaInicio(request.getVigenciaInicio());
        tabela.setVigenciaFim(request.getVigenciaFim());

        // Remove faixas antigas (orphanRemoval = true remove do banco)
        tabela.getFaixas().clear();
        tabelaRepository.flush();  // ← FORÇA o delete antes do inser

        // Agrupa e adiciona novas faixas por nome da categoria
        Map<String, List<FaixaRequest>> faixasPorCategoria = new LinkedHashMap<>();
        for (FaixaRequest fr : request.getFaixas()) {
            String nomeCategoria = fr.getCategoriaNome().toUpperCase();
            faixasPorCategoria.computeIfAbsent(nomeCategoria, k -> new ArrayList<>()).add(fr);
        }

        for (Map.Entry<String, List<FaixaRequest>> entry : faixasPorCategoria.entrySet()) {
            String categoriaNome = entry.getKey();
            List<FaixaRequest> faixasDaCategoria = entry.getValue();

            faixasDaCategoria.sort(Comparator.comparingInt(FaixaRequest::getLimiteInferior));
            validarEscada(faixasDaCategoria, categoriaNome);

            Categoria categoria = categoriaRepository.findByNome(categoriaNome)
                    .orElseThrow(() -> new RegraNegocioException("Categoria " + categoriaNome + " nao encontrada"));

            for (FaixaRequest fr : faixasDaCategoria) {
                Faixa faixa = new Faixa();
                faixa.setTabelaTarifaria(tabela);
                faixa.setCategoria(categoria);
                faixa.setLimiteInferior(fr.getLimiteInferior());
                faixa.setLimiteSuperior(fr.getLimiteSuperior());
                faixa.setValorUnitario(fr.getValorUnitario());
                tabela.getFaixas().add(faixa);
            }
        }

        return tabelaRepository.save(tabela);
    }

    /**
     * Exclui uma tabela tarifária e suas faixas (cascade).
     */
    @Transactional
    public void excluir(Long id) {
        if (!tabelaRepository.existsById(id)) {
            throw new RegraNegocioException("Tabela tarifaria nao encontrada: " + id);
        }
        tabelaRepository.deleteById(id);
    }

    /**
     * Valida as 4 regras de consistência do desafio.
     * O parâmetro categoriaNome é usado apenas para mensagens de erro.
     */
    private void validarEscada(List<FaixaRequest> faixas, String categoriaNome) {
        if (faixas.isEmpty()) return;

        // Regra: cobertura completa - deve iniciar em 0
        if (faixas.get(0).getLimiteInferior() != 0) {
            throw new RegraNegocioException(
                    "Categoria " + categoriaNome + ": primeira faixa deve comecar em 0");
        }

        for (int i = 0; i < faixas.size(); i++) {
            FaixaRequest atual = faixas.get(i);

            // Regra: ordem válida - inferior < superior
            if (atual.getLimiteInferior() >= atual.getLimiteSuperior()) {
                throw new RegraNegocioException(
                        "Categoria " + categoriaNome + ": limite inferior (" + atual.getLimiteInferior() +
                                ") deve ser menor que o superior (" + atual.getLimiteSuperior() + ")");
            }

            // Regra: não sobreposição - sem buracos entre faixas
            if (i < faixas.size() - 1) {
                FaixaRequest proxima = faixas.get(i + 1);
                if (proxima.getLimiteInferior() != atual.getLimiteSuperior() + 1) {
                    throw new RegraNegocioException(
                            "Categoria " + categoriaNome + ": buraco entre faixas. " +
                                    "Esperado inicio em " + (atual.getLimiteSuperior() + 1) +
                                    ", mas recebeu " + proxima.getLimiteInferior());
                }
            }
        }

        // Regra: cobertura suficiente - última faixa deve cobrir consumos elevados
        FaixaRequest ultima = faixas.get(faixas.size() - 1);
        if (ultima.getLimiteSuperior() < 99999) {
            throw new RegraNegocioException(
                    "Categoria " + categoriaNome + ": ultima faixa deve ter limite superior >= 99999");
        }
    }
}