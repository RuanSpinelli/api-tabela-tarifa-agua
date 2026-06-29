package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.CategoriaRequest;
import com.desafio.tarifa.agua.dto.FaixaRequest;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import com.desafio.tarifa.agua.model.Categoria;
import com.desafio.tarifa.agua.model.Faixa;
import com.desafio.tarifa.agua.model.TabelaTarifaria;
import com.desafio.tarifa.agua.model.TabelaTarifariaCategoria;
import com.desafio.tarifa.agua.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TabelaTarifariaCategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ValidacaoFaixaService validacaoFaixaService;

    /**
     * Converte CategoriaRequest em TabelaTarifariaCategoria com suas faixas.
     */
    public List<TabelaTarifariaCategoria> criarCategorias(
            List<CategoriaRequest> categoriasRequest, TabelaTarifaria tabela) {

        List<TabelaTarifariaCategoria> categorias = new ArrayList<>();

        for (CategoriaRequest catRequest : categoriasRequest) {
            String nome = catRequest.getCategoriaNome().toUpperCase();
            Categoria categoria = buscarCategoria(nome);

            TabelaTarifariaCategoria tabCat = new TabelaTarifariaCategoria();
            tabCat.setTabelaTarifaria(tabela);
            tabCat.setCategoria(categoria);
            tabCat.setFaixas(criarFaixas(catRequest.getFaixas(), nome, tabCat));

            categorias.add(tabCat);
        }
        return categorias;
    }

    private List<Faixa> criarFaixas(List<FaixaRequest> faixasRequest,
                                    String categoriaNome,
                                    TabelaTarifariaCategoria tabCat) {
        List<FaixaRequest> faixasOrdenadas = new ArrayList<>(faixasRequest);
        faixasOrdenadas.sort(Comparator.comparingInt(FaixaRequest::getLimiteInferior));
        validacaoFaixaService.validar(faixasOrdenadas, categoriaNome);

        List<Faixa> faixas = new ArrayList<>();
        for (FaixaRequest fr : faixasOrdenadas) {
            Faixa faixa = new Faixa();
            faixa.setTabelaTarifariaCategoria(tabCat);
            faixa.setLimiteInferior(fr.getLimiteInferior());
            faixa.setLimiteSuperior(fr.getLimiteSuperior());
            faixa.setValorUnitario(fr.getValorUnitario());
            faixas.add(faixa);
        }
        return faixas;
    }

    private Categoria buscarCategoria(String nome) {
        return categoriaRepository.findByNome(nome)
                .orElseThrow(() -> {
                    List<String> disponiveis = categoriaRepository.findAll()
                            .stream()
                            .map(Categoria::getNome)
                            .toList();
                    throw new RegraNegocioException(
                            "Categoria '" + nome + "' nao encontrada. Disponiveis: " + disponiveis);
                });
    }
}