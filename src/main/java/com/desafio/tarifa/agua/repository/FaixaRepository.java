package com.desafio.tarifa.agua.repository;

import com.desafio.tarifa.agua.model.Faixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FaixaRepository extends JpaRepository<Faixa, Long> {

    List<Faixa> findByTabelaTarifariaCategoria_TabelaTarifaria_IdAndTabelaTarifariaCategoria_Categoria_IdOrderByLimiteInferiorAsc(
            Long tabelaId, Long categoriaId);
}