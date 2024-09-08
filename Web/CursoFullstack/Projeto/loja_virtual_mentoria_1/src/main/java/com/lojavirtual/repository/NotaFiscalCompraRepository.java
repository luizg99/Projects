package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.NotaFiscalCompraModel;

@Repository
@Transactional
public interface NotaFiscalCompraRepository extends JpaRepository<NotaFiscalCompraModel, Long> {
	
	@Query(value = "select * from NOTAS_FISCAIS_COMPRAS where upper(trim(DESCRICAO_OBSERVACAO)) like %?1%", nativeQuery = true)
	List<NotaFiscalCompraModel> buscarNotaDescricao(String descricao);
	
	@Query(value = "select * from NOTAS_FISCAIS_COMPRAS where PESSOA_ID = ?1", nativeQuery = true)
	List<NotaFiscalCompraModel> buscarNotaPorPessoa(String pessoaId);
	
	@Query(value = "select * from NOTAS_FISCAIS_COMPRAS where CONTA_PAGAR = ?1", nativeQuery = true)
	List<NotaFiscalCompraModel> buscarNotaPorContaPagar(String contaPagarId);
	
	@Query(value = "select * from NOTAS_FISCAIS_COMPRAS where EMPRESA_ID = ?1", nativeQuery = true)
	List<NotaFiscalCompraModel> buscarNotaPorEmpresa(String empresaId);
	
	@Transactional
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query(value = "delete from NOTAS_FISCAIS_COMPRAS_ITENS  where NOTA_FISCAL_COMPRA_ID = ?1", nativeQuery = true)
	void deleteItemNotaFiscalCompra(Long notaFiscalId);
}
