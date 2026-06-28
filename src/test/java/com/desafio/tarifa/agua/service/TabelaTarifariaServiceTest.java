package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.CategoriaRequest;
import com.desafio.tarifa.agua.dto.FaixaRequest;
import com.desafio.tarifa.agua.dto.TabelaTarifariaRequest;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import com.desafio.tarifa.agua.model.TabelaTarifaria;
import com.desafio.tarifa.agua.model.TabelaTarifariaCategoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TabelaTarifariaServiceTest {

    @Autowired
    private TabelaTarifariaService service;

    @Test
    void deveCriarTabelaComFaixasValidas() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Tabela Valida");
        request.setCategorias(List.of(
                criarCategoria("COMERCIAL", List.of(
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 20, BigDecimal.ONE),
                        criarFaixa(21, 30, BigDecimal.ONE),
                        criarFaixa(31, 99999, BigDecimal.ONE)
                ))
        ));

        TabelaTarifaria tabela = service.criar(request);

        assertNotNull(tabela.getId());
        assertEquals("Tabela Valida", tabela.getNome());
        assertEquals(1, tabela.getCategorias().size());
        assertEquals(4, tabela.getCategorias().get(0).getFaixas().size());
    }

    @Test
    void deveRejeitarFaixasSobrepostas() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Sobreposicao");
        request.setCategorias(List.of(
                criarCategoria("COMERCIAL", List.of(
                        criarFaixa(0, 15, BigDecimal.ONE),
                        criarFaixa(10, 20, BigDecimal.ONE)
                ))
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarPrimeiraFaixaNaoComecaEmZero() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Sem Zero");
        request.setCategorias(List.of(
                criarCategoria("COMERCIAL", List.of(
                        criarFaixa(5, 10, BigDecimal.ONE)
                ))
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarFaixaComLimiteInferiorMaiorQueSuperior() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Ordem Invalida");
        request.setCategorias(List.of(
                criarCategoria("COMERCIAL", List.of(
                        criarFaixa(10, 5, BigDecimal.ONE)
                ))
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarBuracoEntreFaixas() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Buraco");
        request.setCategorias(List.of(
                criarCategoria("COMERCIAL", List.of(
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(15, 20, BigDecimal.ONE)
                ))
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarUltimaFaixaComLimiteBaixo() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Cobertura Insuficiente");
        request.setCategorias(List.of(
                criarCategoria("COMERCIAL", List.of(
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 100, BigDecimal.ONE)
                ))
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveListarTabelas() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Tabela Listagem");
        request.setCategorias(List.of(
                criarCategoria("COMERCIAL", List.of(
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 20, BigDecimal.ONE),
                        criarFaixa(21, 30, BigDecimal.ONE),
                        criarFaixa(31, 99999, BigDecimal.ONE)
                ))
        ));
        service.criar(request);

        List<TabelaTarifaria> tabelas = service.listarTodas();
        assertFalse(tabelas.isEmpty());
    }

    @Test
    void deveExcluirTabela() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Tabela Excluir");
        request.setCategorias(List.of(
                criarCategoria("COMERCIAL", List.of(
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 20, BigDecimal.ONE),
                        criarFaixa(21, 30, BigDecimal.ONE),
                        criarFaixa(31, 99999, BigDecimal.ONE)
                ))
        ));
        Long id = service.criar(request).getId();

        service.excluir(id);
        assertTrue(service.listarTodas().isEmpty());
    }

    private CategoriaRequest criarCategoria(String nome, List<FaixaRequest> faixas) {
        CategoriaRequest cat = new CategoriaRequest();
        cat.setCategoriaNome(nome);
        cat.setFaixas(faixas);
        return cat;
    }

    private FaixaRequest criarFaixa(int inicio, int fim, BigDecimal valor) {
        FaixaRequest faixa = new FaixaRequest();
        faixa.setLimiteInferior(inicio);
        faixa.setLimiteSuperior(fim);
        faixa.setValorUnitario(valor);
        return faixa;
    }
}