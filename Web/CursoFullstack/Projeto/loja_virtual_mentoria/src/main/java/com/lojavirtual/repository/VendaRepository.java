package com.lojavirtual.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.VendaModel;

@Repository
@Transactional
public interface VendaRepository extends JpaRepository<VendaModel, Long> {
	
    @Modifying(flushAutomatically = true)
    @Query(
        nativeQuery = true, 
        value = 
            "begin; " +
            "  delete from status_rastreios where venda_id = :id ; " + 
            "  update notas_fiscais_vendas set venda_id = null  where venda_id = :id ; " + 
            "  delete from notas_fiscais_vendas where venda_id = :id ; " +  
            "  delete from vendas_itens where venda_id = :id ; " +  
            "  delete from vendas where id = :id ; " +
            "  commit; " +
            "end; " 
    )
    void deletarVenda(@Param("id") Long id);

    @Modifying(flushAutomatically = true)
    @Query(
        nativeQuery = true, 
        value = 
            "begin; " +
            "  update vendas set excluido = :excluido  where id = :id ; " + 
            "  commit; " +
            "end; " 
    )
    void mudarStatusVenda(@Param("id") Long id, @Param("excluido") Boolean excluido);

    @Query(nativeQuery = true, value = "select * from VENDAS where id = ?1 and EXCLUIDO = false")
    Optional<VendaModel> findByIdExclusao(Long id);

    @Query(nativeQuery = true, value = "select distinct * from VENDAS where ID in( select VENDA_ID from VENDAS_ITENS where PRODUTO_ID = :produtoId ) and EXCLUIDO = FALSE " )
    List<VendaModel> findVendasPorProdutoId(@Param("produtoId") Long produtoId);

    @Query(nativeQuery = true, value = "select distinct * from VENDAS where ID in( select VENDA_ID from VENDAS_ITENS where PRODUTO_ID in (select ID from PRODUTOS where upper(NOME) like CONCAT('%', :nomeProduto, '%')) ) and EXCLUIDO = FALSE " )
    List<VendaModel> findVendasPorNomeProduto(@Param("nomeProduto") String nomeProduto);

    @Query(nativeQuery = true, value = "select distinct * from VENDAS where PESSOA_ID in( select ID from PESSOAS_FISICAS where upper(NOME) like CONCAT('%', :nomeCliente, '%') ) and EXCLUIDO = FALSE " )
    List<VendaModel> findVendasPorNomeCliente(@Param("nomeCliente") String nomeCliente);

    @Query(nativeQuery = true, value = "select distinct * from VENDAS where ENDERECO_COBRANCA_ID in( select ID from ENDERECOS where upper(LOGRADOURO) like CONCAT('%', :logradouro, '%') and TIPO_ENDERECO = :tipoEndereco ) and EXCLUIDO = FALSE " )
    List<VendaModel> findVendasPorEndereco(@Param("logradouro") String logradouro, @Param("tipoEndereco") String tipoEndereco);

    List<VendaModel> findByDataVendaBetweenAndExcluidoFalse(Date dataInicio, Date dataFim);

    List<VendaModel> findByPessoaIdAndExcluidoFalse(Long pessoaId);

    @Query(nativeQuery = true, value = "select distinct * from VENDAS where PESSOA_ID in( select ID from PESSOAS_FISICAS where upper(CPF) like CONCAT('%', :cpf, '%') ) and EXCLUIDO = FALSE " )
    List<VendaModel> findVendasPorCpfCliente(@Param("cpf") String cpf);
}

