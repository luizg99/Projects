package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.AvaliacaoProdutoModel;

@Repository
@Transactional
public interface AvaliacaoProdutoRepository extends JpaRepository<AvaliacaoProdutoModel, Long> {
	
	@Query(value = "select * from AVALIACOES_PRODUTOS where PRODUTO_ID = ?1", nativeQuery = true)
	List<AvaliacaoProdutoModel> avaliacoesProdutos(Long produtoId);
	
	@Query(value = "select * from AVALIACOES_PRODUTOS where PRODUTO_ID = ?1 and PESSOA_ID = ?2", nativeQuery = true)
	List<AvaliacaoProdutoModel> avaliacoesProdutosPessoa(Long produtoId, Long pessoaId);
	
	@Query(value = "select * from AVALIACOES_PRODUTOS where PESSOA_ID = ?2", nativeQuery = true)
	List<AvaliacaoProdutoModel> avaliacoesPessoa(Long pessoaId);
}
