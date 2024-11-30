package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.ProdutoModel;

@Repository
@Transactional
public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long> {
	
	@Query(value = "select count(1) > 0 from PRODUTOS where upper(trim(NOME)) = upper(trim(?1)) ", nativeQuery = true)
	public boolean existeProduto(String nome);
	
	@Query(value = "select count(1) > 0 from PRODUTOS where upper(trim(NOME)) = upper(trim(?1)) and EMPRESA_ID = ?2 ", nativeQuery = true)
	public boolean existeProduto(String nome, Long empresaId);
	
	@Query(value = "select * from PRODUTOS  where upper(trim(NOME)) like %?1%", nativeQuery = true)
	List<ProdutoModel> buscarProdutoNome(String nome);

	@Query(value = "select * from PRODUTOS  where upper(trim(NOME)) like %?1% and EMPRESA_ID = ?2 ", nativeQuery = true)
	List<ProdutoModel> buscarProdutoNome(String nome, Long empresaId);
}
