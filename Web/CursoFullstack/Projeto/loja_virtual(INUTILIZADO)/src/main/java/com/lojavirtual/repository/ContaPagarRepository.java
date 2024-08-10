package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.ContaPagarModel;

@Repository
@Transactional
public interface ContaPagarRepository extends JpaRepository<ContaPagarModel, Long>{
	@Query(nativeQuery = true, value = "select * from CONTAS_PAGAR where upper(trim(DESCRICAO)) like %?1%")
	List<ContaPagarModel> buscarContasPagarDesc(String descricao);
	
	@Query(nativeQuery = true, value = "select * from CONTAS_PAGAR where PESSOA_ID = ?1")
	List<ContaPagarModel> buscarContasPagarPorPessoa(String pessoaId);
	
	@Query(nativeQuery = true, value = "select * from CONTAS_PAGAR where FORNECEDOR_ID = ?1")
	List<ContaPagarModel> buscarContasPagarPorFornecedor(String fornecedorId);
	
	@Query(nativeQuery = true, value = "select * from CONTAS_PAGAR where EMPRESA_ID = ?1")
	List<ContaPagarModel> buscarContasPagarPorEmpresa(String empresaId);
}
