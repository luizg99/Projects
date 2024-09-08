package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.CategoriaProdutoModel;

@Repository
@Transactional
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProdutoModel, Long> {
	
	@Query(value = "select count(1) > 0 from CATEGORIAS_PRODUTOS where upper(trim(descricao)) = upper(trim(?1)) ", nativeQuery = true)
	public boolean existeCategoria(String descricao);
	
	@Query(value = "select * from CATEGORIAS_PRODUTOS where upper(trim(descricao)) like %?1% ", nativeQuery = true)
	public List<CategoriaProdutoModel> buscarCategoriaDescricao(String descricao);
}
