package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.CategoriaProduto;

@Repository
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long>{
	@Query(nativeQuery = true, value =  "select count(1) > 0 from CATEGORIAS_PRODUTOS where upper(trim(DESCRICAO)) = upper(trim(?1));")
	public boolean existeCategoria(String nomeCategoria);

	@Query(value = "select * from CATEGORIAS_PRODUTOS a where upper(trim(a.DESCRICAO)) like %?1%", nativeQuery = true)
	public List<CategoriaProduto> buscarCategoriaDescricao(String descricao);
}
