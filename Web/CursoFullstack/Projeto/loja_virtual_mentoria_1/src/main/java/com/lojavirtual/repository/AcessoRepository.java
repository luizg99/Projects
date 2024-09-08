package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.AcessoModel;

@Repository
@Transactional
public interface AcessoRepository extends JpaRepository<AcessoModel, Long> {
	
	@Query(value = "select * from acessos a where upper(trim(a.DESCRICAO)) like %?1%", nativeQuery = true)
	List<AcessoModel> buscarAcessoDescricao(String descricao);
}
