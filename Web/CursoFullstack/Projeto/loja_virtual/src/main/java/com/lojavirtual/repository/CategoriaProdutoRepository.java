package com.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.CategoriaProduto;

@Repository
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long>{
	@Query(nativeQuery = true, value =  "select count(1) > 0 from CATEGORIAS_PRODUTOS where upper(trim(DESCRICAO)) = upper(trim(?1));")
	public boolean existeCategoria(String nomeCategoria);
}
