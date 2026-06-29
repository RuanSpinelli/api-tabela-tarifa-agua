package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.CategoriaRequest;
import com.desafio.tarifa.agua.dto.FaixaRequest;
import com.desafio.tarifa.agua.dto.TabelaTarifariaRequest;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import com.desafio.tarifa.agua.model.TabelaTarifaria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TabelaTarifariaServiceTest {

    @Autowired
    private TabelaTarifariaService service;

    @Test
    void deveCriarTabelaComFaixasValidas() {
        TabelaTarifariaRequest request = criarRequest("Tabela Valida",
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 20, BigDecimal.ONE),
                        criarFaixa(21, 30, BigDecimal.ONE),
                        criarFaixa(31, 99999, BigDecimal.ONE)
                )
        );

        TabelaTarifaria tabela = service.criar(request);

        assertNotNull(tabela.getId());
        assertEquals("Tabela Valida", tabela.getNome());
        assertEquals(1, tabela.getCategorias().size());
        assertEquals(4, tabela.getCategorias().get(0).getFaixas().size());
    }

    @Test
    void deveRejeitarFaixasSobrepostas() {
        TabelaTarifariaRequest request = criarRequest("Teste Sobreposicao",
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 15, BigDecimal.ONE),
                        criarFaixa(10, 20, BigDecimal.ONE),
                        criarFaixa(21, 99999, BigDecimal.ONE)
                )
        );

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarPrimeiraFaixaNaoComecaEmZero() {
        TabelaTarifariaRequest request = criarRequest("Teste Sem Zero",
                criarCategoria("COMERCIAL",
                        criarFaixa(5, 10, BigDecimal.ONE),
                        criarFaixa(11, 20, BigDecimal.ONE),
                        criarFaixa(21, 99999, BigDecimal.ONE)
                )
        );

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarFaixaComLimiteInferiorMaiorQueSuperior() {
        TabelaTarifariaRequest request = criarRequest("Teste Ordem Invalida",
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(20, 11, BigDecimal.ONE),
                        criarFaixa(31, 99999, BigDecimal.ONE)
                )
        );

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarBuracoEntreFaixas() {
        TabelaTarifariaRequest request = criarRequest("Teste Buraco",
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(15, 20, BigDecimal.ONE),
                        criarFaixa(21, 99999, BigDecimal.ONE)
                )
        );

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarUltimaFaixaComLimiteBaixo() {
        TabelaTarifariaRequest request = criarRequest("Teste Cobertura",
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 100, BigDecimal.ONE)
                )
        );

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveRejeitarCategoriaInexistente() {
        TabelaTarifariaRequest request = criarRequest("Teste Categoria",
                criarCategoria("HOSPITALAR",
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 20, BigDecimal.ONE),
                        criarFaixa(21, 99999, BigDecimal.ONE)
                )
        );

        assertThrows(RegraNegocioException.class, () -> service.criar(request));
    }

    @Test
    void deveListarTabelas() {
        service.criar(criarRequest("Tabela Listagem",
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 20, BigDecimal.ONE),
                        criarFaixa(21, 30, BigDecimal.ONE),
                        criarFaixa(31, 99999, BigDecimal.ONE)
                )
        ));

        assertFalse(service.listarTodas().isEmpty());
    }

    @Test
    void deveExcluirTabela() {
        Long id = service.criar(criarRequest("Tabela Excluir",
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 20, BigDecimal.ONE),
                        criarFaixa(21, 30, BigDecimal.ONE),
                        criarFaixa(31, 99999, BigDecimal.ONE)
                )
        )).getId();

        service.excluir(id);
        assertTrue(service.listarTodas().isEmpty());
    }

    @Test
    void deveAtualizarTabela() {
        Long id = service.criar(criarRequest("Original",
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 10, BigDecimal.ONE),
                        criarFaixa(11, 20, BigDecimal.ONE),
                        criarFaixa(21, 30, BigDecimal.ONE),
                        criarFaixa(31, 99999, BigDecimal.ONE)
                )
        )).getId();

        TabelaTarifaria atualizada = service.atualizar(id, criarRequest("Atualizada",
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 10, new BigDecimal("10.00")),
                        criarFaixa(11, 20, new BigDecimal("15.00")),
                        criarFaixa(21, 30, new BigDecimal("20.00")),
                        criarFaixa(31, 99999, new BigDecimal("25.00"))
                )
        ));

        assertEquals("Atualizada", atualizada.getNome());
        assertEquals(new BigDecimal("10.00"), atualizada.getCategorias().get(0).getFaixas().get(0).getValorUnitario());
    }

    // Métodos auxiliares
    private TabelaTarifariaRequest criarRequest(String nome, CategoriaRequest... categorias) {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome(nome);
        request.setCategorias(List.of(categorias));
        return request;
    }

    private CategoriaRequest criarCategoria(String nome, FaixaRequest... faixas) {
        CategoriaRequest cat = new CategoriaRequest();
        cat.setCategoriaNome(nome);
        cat.setFaixas(List.of(faixas));
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