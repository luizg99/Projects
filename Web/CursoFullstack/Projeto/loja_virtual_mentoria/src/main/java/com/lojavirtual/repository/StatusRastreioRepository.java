package com.lojavirtual.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.StatusRastreioModel;

@Repository
@Transactional
public interface StatusRastreioRepository extends JpaRepository<StatusRastreioModel, Long> {
	
	List<StatusRastreioModel> findByVendaIdOrderById(Long vendaId);
	
	//@Query(value = "select s from STATUS_RASTREIOS s where s.venda_id = ?1 order by s.id")
	//public List<StatusRastreioModel> listaRastreioVenda(Long idVenda);

	//@Modifying(flushAutomatically = true)
	//@Query(nativeQuery = true, value = "update VENDAS set URL_RASTREIO = ?1 where id = ?2")
	//public void salvarUrlRastreio(String urlRastreio, Long idVenda);
}
