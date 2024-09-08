package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.ImagemProdutoModel;

@Repository
@Transactional
public interface ImagemProdutoRepository extends JpaRepository<ImagemProdutoModel, Long> {
	
	@Query(value = "select * from IMAGENS_PRODUTOS where PRODUTO_ID = ?1 ", nativeQuery = true)
	public List<ImagemProdutoModel> buscarImagensProduto(Long produtoId);
	
	@Modifying(flushAutomatically = true)
	@Transactional
	@Query(value = "delete from IMAGENS_PRODUTOS where PRODUTO_ID = ?1 ", nativeQuery = true)
	public void deletarImagensProduto(Long produtoId);
}
