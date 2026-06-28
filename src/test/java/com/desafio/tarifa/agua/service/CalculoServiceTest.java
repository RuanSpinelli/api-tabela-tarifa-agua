package com.desafio.tarifa.agua.service;

import com.desafio.tarifa.agua.dto.CalculoRequest;
import com.desafio.tarifa.agua.dto.CalculoResponse;
import com.desafio.tarifa.agua.dto.FaixaRequest;
import com.desafio.tarifa.agua.dto.TabelaTarifariaRequest;
import com.desafio.tarifa.agua.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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
        request.setFaixas(List.of(
                criarFaixa("INDUSTRIAL", 0, 10, new BigDecimal("1.00")),
                criarFaixa("INDUSTRIAL", 11, 20, new BigDecimal("2.00")),
                criarFaixa("INDUSTRIAL", 21, 30, new BigDecimal("3.00")),
                criarFaixa("INDUSTRIAL", 31, 99999, new BigDecimal("4.00"))
        ));
        tabelaId = tabelaService.criar(request).getId();
    }

    @Test
    void deveCalcularExemploDoEnunciado() {
        CalculoRequest request = new CalculoRequest();
        request.setTabelaId(tabelaId);
        request.setCategoria("INDUSTRIAL");
        request.setConsumo(18);

        CalculoResponse response = calculoService.calcular(request);

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
        CalculoRequest request = new CalculoRequest();
        request.setTabelaId(tabelaId);
        request.setCategoria("INDUSTRIAL");
        request.setConsumo(0);

        CalculoResponse response = calculoService.calcular(request);

        assertEquals(0, response.getConsumoTotal());
        assertEquals(BigDecimal.ZERO, response.getValorTotal());
        assertTrue(response.getDetalhamento().isEmpty());
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

    private FaixaRequest criarFaixa(String categoriaNome, int inicio, int fim, BigDecimal valor) {
        FaixaRequest faixa = new FaixaRequest();
        faixa.setCategoriaNome(categoriaNome);
        faixa.setLimiteInferior(inicio);
        faixa.setLimiteSuperior(fim);
        faixa.setValorUnitario(valor);
        return faixa;
    }
}