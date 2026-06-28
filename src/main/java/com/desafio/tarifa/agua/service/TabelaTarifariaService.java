package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.CategoriaRequest;
import com.desafio.tarifa.agua.dto.FaixaRequest;
import com.desafio.tarifa.agua.dto.TabelaTarifariaRequest;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import com.desafio.tarifa.agua.model.Categoria;
import com.desafio.tarifa.agua.model.Faixa;
import com.desafio.tarifa.agua.model.TabelaTarifaria;
import com.desafio.tarifa.agua.model.TabelaTarifariaCategoria;
import com.desafio.tarifa.agua.repository.CategoriaRepository;
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
     * Cria uma nova tabela tarifária com categorias e suas respectivas faixas.
     * Estrutura: TabelaTarifaria -> TabelaTarifariaCategoria -> Faixa
     */
    @Transactional
    public TabelaTarifaria criar(TabelaTarifariaRequest request) {
        TabelaTarifaria tabela = new TabelaTarifaria();
        tabela.setNome(request.getNome());
        tabela.setVigenciaInicio(request.getVigenciaInicio());
        tabela.setVigenciaFim(request.getVigenciaFim());

        List<TabelaTarifariaCategoria> categoriasEntidade = new ArrayList<>();

        for (CategoriaRequest catRequest : request.getCategorias()) {
            String categoriaNome = catRequest.getCategoriaNome().toUpperCase();

            // Busca a categoria pelo nome
            Categoria categoria = categoriaRepository.findByNome(categoriaNome)
                    .orElseThrow(() -> new RegraNegocioException("Categoria " + categoriaNome + " nao encontrada"));

            // Cria o vínculo TabelaTarifariaCategoria
            TabelaTarifariaCategoria tabCat = new TabelaTarifariaCategoria();
            tabCat.setTabelaTarifaria(tabela);
            tabCat.setCategoria(categoria);

            // Ordena e valida as faixas
            List<FaixaRequest> faixasRequest = catRequest.getFaixas();
            faixasRequest.sort(Comparator.comparingInt(FaixaRequest::getLimiteInferior));
            validarEscada(faixasRequest, categoriaNome);

            // Cria as faixas vinculadas ao TabelaTarifariaCategoria
            List<Faixa> faixasEntidade = new ArrayList<>();
            for (FaixaRequest fr : faixasRequest) {
                Faixa faixa = new Faixa();
                faixa.setTabelaTarifariaCategoria(tabCat);
                faixa.setLimiteInferior(fr.getLimiteInferior());
                faixa.setLimiteSuperior(fr.getLimiteSuperior());
                faixa.setValorUnitario(fr.getValorUnitario());
                faixasEntidade.add(faixa);
            }
            tabCat.setFaixas(faixasEntidade);
            categoriasEntidade.add(tabCat);
        }

        tabela.setCategorias(categoriasEntidade);
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
     */
    @Transactional
    public TabelaTarifaria atualizar(Long id, TabelaTarifariaRequest request) {
        TabelaTarifaria tabela = tabelaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Tabela tarifaria nao encontrada: " + id));

        tabela.setNome(request.getNome());
        tabela.setVigenciaInicio(request.getVigenciaInicio());
        tabela.setVigenciaFim(request.getVigenciaFim());

        // Remove categorias e faixas antigas
        tabela.getCategorias().clear();
        tabelaRepository.flush();

        // Adiciona novas
        List<TabelaTarifariaCategoria> categoriasEntidade = new ArrayList<>();

        for (CategoriaRequest catRequest : request.getCategorias()) {
            String categoriaNome = catRequest.getCategoriaNome().toUpperCase();

            Categoria categoria = categoriaRepository.findByNome(categoriaNome)
                    .orElseThrow(() -> new RegraNegocioException("Categoria " + categoriaNome + " nao encontrada"));

            TabelaTarifariaCategoria tabCat = new TabelaTarifariaCategoria();
            tabCat.setTabelaTarifaria(tabela);
            tabCat.setCategoria(categoria);

            List<FaixaRequest> faixasRequest = catRequest.getFaixas();
            faixasRequest.sort(Comparator.comparingInt(FaixaRequest::getLimiteInferior));
            validarEscada(faixasRequest, categoriaNome);

            List<Faixa> faixasEntidade = new ArrayList<>();
            for (FaixaRequest fr : faixasRequest) {
                Faixa faixa = new Faixa();
                faixa.setTabelaTarifariaCategoria(tabCat);
                faixa.setLimiteInferior(fr.getLimiteInferior());
                faixa.setLimiteSuperior(fr.getLimiteSuperior());
                faixa.setValorUnitario(fr.getValorUnitario());
                faixasEntidade.add(faixa);
            }
            tabCat.setFaixas(faixasEntidade);
            categoriasEntidade.add(tabCat);
        }

        tabela.setCategorias(categoriasEntidade);
        return tabelaRepository.save(tabela);
    }

    /**
     * Exclui uma tabela tarifária e tudo vinculado (cascade).
     */
    @Transactional
    public void excluir(Long id) {
        if (!tabelaRepository.existsById(id)) {
            throw new RegraNegocioException("Tabela tarifaria nao encontrada: " + id);
        }
        tabelaRepository.deleteById(id);
    }

    private void validarEscada(List<FaixaRequest> faixas, String categoriaNome) {
        if (faixas.isEmpty()) return;

        if (faixas.get(0).getLimiteInferior() != 0) {
            throw new RegraNegocioException("Categoria " + categoriaNome + ": primeira faixa deve comecar em 0");
        }

        for (int i = 0; i < faixas.size(); i++) {
            FaixaRequest atual = faixas.get(i);

            if (atual.getLimiteInferior() >= atual.getLimiteSuperior()) {
                throw new RegraNegocioException(
                        "Categoria " + categoriaNome + ": limite inferior (" + atual.getLimiteInferior() +
                                ") deve ser menor que o superior (" + atual.getLimiteSuperior() + ")");
            }

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

        FaixaRequest ultima = faixas.get(faixas.size() - 1);
        if (ultima.getLimiteSuperior() < 99999) {
            throw new RegraNegocioException(
                    "Categoria " + categoriaNome + ": ultima faixa deve ter limite superior >= 99999");
        }
    }
}