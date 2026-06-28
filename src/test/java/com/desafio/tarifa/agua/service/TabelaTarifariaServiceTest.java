package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.FaixaRequest;
import com.desafio.tarifa.agua.dto.TabelaTarifariaRequest;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import com.desafio.tarifa.agua.model.TabelaTarifaria;
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
        request.setFaixas(List.of(
                criarFaixa("COMERCIAL", 0, 10, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 11, 20, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 21, 30, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 31, 99999, BigDecimal.ONE)
        ));

        TabelaTarifaria tabela = service.criar(request);

        assertNotNull(tabela.getId());
        assertEquals("Tabela Valida", tabela.getNome());
        assertEquals(4, tabela.getFaixas().size());
    }

    @Test
    void deveRejeitarFaixasSobrepostas() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Sobreposicao");
        request.setFaixas(List.of(
                criarFaixa("COMERCIAL", 0, 15, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 10, 20, BigDecimal.ONE)
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarPrimeiraFaixaNaoComecaEmZero() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Sem Zero");
        request.setFaixas(List.of(
                criarFaixa("COMERCIAL", 5, 10, BigDecimal.ONE)
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarFaixaComLimiteInferiorMaiorQueSuperior() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Ordem Invalida");
        request.setFaixas(List.of(
                criarFaixa("COMERCIAL", 10, 5, BigDecimal.ONE)
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarBuracoEntreFaixas() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Buraco");
        request.setFaixas(List.of(
                criarFaixa("COMERCIAL", 0, 10, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 15, 20, BigDecimal.ONE)
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarUltimaFaixaComLimiteBaixo() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Teste Cobertura Insuficiente");
        request.setFaixas(List.of(
                criarFaixa("COMERCIAL", 0, 10, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 11, 100, BigDecimal.ONE)
        ));

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveListarTabelas() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Tabela Listagem");
        request.setFaixas(List.of(
                criarFaixa("COMERCIAL", 0, 10, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 11, 20, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 21, 30, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 31, 99999, BigDecimal.ONE)
        ));
        service.criar(request);

        List<TabelaTarifaria> tabelas = service.listarTodas();
        assertFalse(tabelas.isEmpty());
    }

    @Test
    void deveExcluirTabela() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Tabela Excluir");
        request.setFaixas(List.of(
                criarFaixa("COMERCIAL", 0, 10, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 11, 20, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 21, 30, BigDecimal.ONE),
                criarFaixa("COMERCIAL", 31, 99999, BigDecimal.ONE)
        ));
        Long id = service.criar(request).getId();

        service.excluir(id);
        assertTrue(service.listarTodas().isEmpty());
    }

    private FaixaRequest criarFaixa(String categoriaNome, int inicio, int fim, BigDecimal valor) {
        FaixaRequest faixa = new FaixaRequest();
        faixa.setCategoriaNome(categoriaNome);
        faixa.setLimiteInferior(inicio);
        faixa.setLimiteSuperior(fim);
        faixa.setValorUnitario(valor);
        return faixa;
    }
}