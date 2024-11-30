package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.CategoriaProduto;
import com.lojavirtual.model.Produtos;

import jakarta.transaction.Transactional;

//Definindo que é um repositório
@Repository
//Definindo para o repositório gerenciar as transações com o banco de dados.
@Transactional
public interface ProdutoRepository extends JpaRepository<Produtos, Long>{
	@Query(value = "select count(1) > 0 from PRODUTOS where upper(trim(DESCRICAO)) = upper(trim(?1)) ", nativeQuery = true)
	public boolean existeProduto(String nome);
	
	@Query(value = "select count(1) > 0 from PRODUTOS where upper(trim(DESCRICAO)) = upper(trim(?1)) and EMPRESA_ID = ?2 ", nativeQuery = true)
	public boolean existeProduto(String nome, Long empresaId);
	
	@Query(value = "select * from PRODUTOS  where upper(trim(DESCRICAO)) like %?1%", nativeQuery = true)
	List<Produtos> buscarProdutoNome(String nome);

	@Query(value = "select * from PRODUTOS  where upper(trim(DESCRICAO)) like %?1% and EMPRESA_ID = ?2 ", nativeQuery = true)
	List<Produtos> buscarProdutoNome(String nome, Long empresaId);
}
