package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.NotaItemCompraModel;

@Repository
@Transactional
public interface NotaFiscalItemCompraRepository extends JpaRepository<NotaItemCompraModel, Long> {
	
	@Query(value = "select * from NOTAS_FISCAIS_COMPRAS_ITENS where PRODUTO_ID = ?1 and NOTA_FISCAL_COMPRA_ID = ?2", nativeQuery = true)
	List<NotaItemCompraModel> buscarNotaItemPorProdutoNota(Long produtoId, Long notaFiscalId);
	
	@Query(value = "select * from NOTAS_FISCAIS_COMPRAS_ITENS where PRODUTO_ID = ?1", nativeQuery = true)
	List<NotaItemCompraModel> buscarNotaItemPorProduto(Long produtoId);
	
	@Query(value = "select * from NOTAS_FISCAIS_COMPRAS_ITENS where NOTA_FISCAL_COMPRA_ID = ?1", nativeQuery = true)
	List<NotaItemCompraModel> buscarNotaItemPorNotaCompra(Long notaFiscalId);
	
	@Query(value = "select * from NOTAS_FISCAIS_COMPRAS_ITENS where EMPRESA_ID = ?1", nativeQuery = true)
	List<NotaItemCompraModel> buscarNotaItemPorEmpresa(Long empresaId);
}
