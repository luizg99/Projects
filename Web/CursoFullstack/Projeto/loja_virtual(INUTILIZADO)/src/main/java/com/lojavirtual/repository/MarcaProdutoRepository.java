package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.lojavirtual.model.MarcaProdutoModel;

import jakarta.transaction.Transactional;

//Definindo que é um repositório
@Repository
//Definindo para o repositório gerenciar as transações com o banco de dados.
@Transactional
public interface MarcaProdutoRepository extends JpaRepository<MarcaProdutoModel, Long>{
	@Query(value = "select count(1) > 0 from MARCAS_PRODUTOS where upper(trim(DESCRICAO)) = upper(trim(?1)) ", nativeQuery = true)
	public boolean existeMarcaProduto(String descricao);
	
	@Query(value = "select count(1) > 0 from MARCAS_PRODUTOS where upper(trim(DESCRICAO)) = upper(trim(?1)) and EMPRESA_ID = ?2 ", nativeQuery = true)
	public boolean existeMarcaProduto(String descricao, Long empresaId);
	
	@Query(value = "select * from MARCAS_PRODUTOS where upper(trim(DESCRICAO)) like %?1%", nativeQuery = true)
	List<MarcaProdutoModel> buscarMarcaProdutoDescricao(String descricao);

	@Query(value = "select * from MARCAS_PRODUTOS  where upper(trim(DESCRICAO)) like %?1% and EMPRESA_ID = ?2 ", nativeQuery = true)
	List<MarcaProdutoModel> buscarMarcaProdutoDescricao(String descricao, Long empresaId);
}
