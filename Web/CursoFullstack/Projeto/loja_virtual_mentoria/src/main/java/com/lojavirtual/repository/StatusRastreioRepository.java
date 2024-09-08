package com.lojavirtual.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.StatusRastreioModel;

@Repository
@Transactional
public interface StatusRastreioRepository extends JpaRepository<StatusRastreioModel, Long> {
	
	List<StatusRastreioModel> findByVendaIdOrderById(Long vendaId);
}
