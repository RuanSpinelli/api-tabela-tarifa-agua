package com.desafio.tarifa.agua.repository;

import com.desafio.tarifa.agua.model.TabelaTarifaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TabelaTarifariaRepository extends JpaRepository<TabelaTarifaria, Long> {
}