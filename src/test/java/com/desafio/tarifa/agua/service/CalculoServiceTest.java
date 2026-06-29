package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.*;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
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
class CalculoServiceTest {

    @Autowired
    private CalculoService calculoService;

    @Autowired
    private TabelaTarifariaService tabelaService;

    private Long tabelaId;

    @BeforeEach
    void setUp() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Tabela Teste");
        request.setCategorias(List.of(
                criarCategoria("INDUSTRIAL",
                        criarFaixa(0, 10, new BigDecimal("1.00")),
                        criarFaixa(11, 20, new BigDecimal("2.00")),
                        criarFaixa(21, 30, new BigDecimal("3.00")),
                        criarFaixa(31, 99999, new BigDecimal("4.00"))
                )
        ));
        tabelaId = tabelaService.criar(request).getId();
    }

    @Test
    void deveCalcularExemploDoEnunciado() {
        CalculoResponse response = calcular("INDUSTRIAL", 18);

        assertEquals("INDUSTRIAL", response.getCategoria());
        assertEquals(18, response.getConsumoTotal());
        assertEquals(new BigDecimal("26.00"), response.getValorTotal());
        assertEquals(2, response.getDetalhamento().size());
        assertEquals(10, response.getDetalhamento().get(0).getM3Cobrados());
        assertEquals(new BigDecimal("10.00"), response.getDetalhamento().get(0).getSubtotal());
        assertEquals(8, response.getDetalhamento().get(1).getM3Cobrados());
        assertEquals(new BigDecimal("16.00"), response.getDetalhamento().get(1).getSubtotal());
    }

    @Test
    void deveCalcularConsumoZero() {
        CalculoResponse response = calcular("INDUSTRIAL", 0);
        assertEquals(BigDecimal.ZERO, response.getValorTotal());
        assertTrue(response.getDetalhamento().isEmpty());
    }

    @Test
    void deveCalcularConsumoAteUltimaFaixa() {
        CalculoResponse response = calcular("INDUSTRIAL", 50);
        assertEquals(new BigDecimal("140.00"), response.getValorTotal());
    }

    @Test
    void deveCalcularComValoresDecimais() {
        TabelaTarifariaRequest request = new TabelaTarifariaRequest();
        request.setNome("Tabela Decimal");
        request.setCategorias(List.of(
                criarCategoria("COMERCIAL",
                        criarFaixa(0, 10, new BigDecimal("1.75")),
                        criarFaixa(11, 20, new BigDecimal("3.50")),
                        criarFaixa(21, 30, new BigDecimal("5.25")),
                        criarFaixa(31, 99999, new BigDecimal("7.80"))
                )
        ));
        Long idDecimal = tabelaService.criar(request).getId();

        CalculoRequest calcRequest = new CalculoRequest();
        calcRequest.setTabelaId(idDecimal);
        calcRequest.setCategoria("COMERCIAL");
        calcRequest.setConsumo(25);

        CalculoResponse response = calculoService.calcular(calcRequest);
        assertEquals(new BigDecimal("78.75"), response.getValorTotal());
    }

    @Test
    void deveLancarErroQuandoConsumoExcedeCobertura() {
        CalculoRequest request = new CalculoRequest();
        request.setTabelaId(tabelaId);
        request.setCategoria("INDUSTRIAL");
        request.setConsumo(100000);
        assertThrows(RegraNegocioException.class, () -> calculoService.calcular(request));
    }

    @Test
    void deveLancarErroQuandoCategoriaNaoExiste() {
        CalculoRequest request = new CalculoRequest();
        request.setTabelaId(tabelaId);
        request.setCategoria("INEXISTENTE");
        request.setConsumo(10);
        assertThrows(RegraNegocioException.class, () -> calculoService.calcular(request));
    }

    @Test
    void deveLancarErroQuandoTabelaNaoExiste() {
        CalculoRequest request = new CalculoRequest();
        request.setTabelaId(999L);
        request.setCategoria("INDUSTRIAL");
        request.setConsumo(10);
        assertThrows(RegraNegocioException.class, () -> calculoService.calcular(request));
    }

    private CalculoResponse calcular(String categoria, int consumo) {
        CalculoRequest request = new CalculoRequest();
        request.setTabelaId(tabelaId);
        request.setCategoria(categoria);
        request.setConsumo(consumo);
        return calculoService.calcular(request);
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