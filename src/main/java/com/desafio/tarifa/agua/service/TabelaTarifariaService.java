package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.TabelaTarifariaRequest;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import com.desafio.tarifa.agua.model.TabelaTarifaria;
import com.desafio.tarifa.agua.repository.TabelaTarifariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TabelaTarifariaService {

    @Autowired
    private TabelaTarifariaRepository tabelaRepository;

    @Autowired
    private TabelaTarifariaCategoriaService categoriaService;

    @Transactional
    public TabelaTarifaria criar(TabelaTarifariaRequest request) {
        TabelaTarifaria tabela = new TabelaTarifaria();
        tabela.setNome(request.getNome());
        tabela.setVigenciaInicio(request.getVigenciaInicio());
        tabela.setVigenciaFim(request.getVigenciaFim());

        tabela.getCategorias().addAll(categoriaService.criarCategorias(request.getCategorias(), tabela));
        return tabelaRepository.save(tabela);
    }

    public List<TabelaTarifaria> listarTodas() {
        return tabelaRepository.findAll();
    }

    @Transactional
    public TabelaTarifaria atualizar(Long id, TabelaTarifariaRequest request) {
        TabelaTarifaria tabela = tabelaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Tabela tarifaria nao encontrada: " + id));

        tabela.setNome(request.getNome());
        tabela.setVigenciaInicio(request.getVigenciaInicio());
        tabela.setVigenciaFim(request.getVigenciaFim());

        tabela.getCategorias().clear();
        tabelaRepository.flush();

        // Passa a referência da tabela
        tabela.getCategorias().addAll(categoriaService.criarCategorias(request.getCategorias(), tabela));
        return tabelaRepository.save(tabela);
    }

    @Transactional
    public void excluir(Long id) {
        if (!tabelaRepository.existsById(id)) {
            throw new RegraNegocioException("Tabela tarifaria nao encontrada: " + id);
        }
        tabelaRepository.deleteById(id);
    }
}