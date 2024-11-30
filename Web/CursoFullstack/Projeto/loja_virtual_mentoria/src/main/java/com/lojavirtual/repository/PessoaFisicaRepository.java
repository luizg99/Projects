package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.PessoaFisicaModel;

@Repository
public interface PessoaFisicaRepository extends CrudRepository<PessoaFisicaModel, Long> {

	@Query(value = "select * from PESSOAS_FISICAS where CPF = ?1", nativeQuery = true)
	public PessoaFisicaModel existeCpfCadastrado(String cpf);
	
	@Query(value = "select * from PESSOAS_FISICAS where CPF = ?1", nativeQuery = true)
	public List<PessoaFisicaModel> pesquisaPorCpf(String cpf);
	
	@Query(value = "select * from PESSOAS_FISICAS where upper(trim(NOME)) like %?1%", nativeQuery = true)
	public List<PessoaFisicaModel> pesquisaPorNome(String nome);
}
