package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.PessoaJuridicaModel;

@Repository
public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridicaModel, Long> {
	
	@Query(value = "select * from PESSOAS_JURIDICAS where CNPJ = ?1", nativeQuery = true)
	public PessoaJuridicaModel existeCnpjCadastrado(String cnpj);
	
	@Query(value = "select * from PESSOAS_JURIDICAS where INSCRICAO_ESTADUAL = ?1", nativeQuery = true)
	public PessoaJuridicaModel existeInscEstadualCadastrado(String inscricaoEstadual);
	
	@Query(value = "select * from PESSOAS_JURIDICAS where CNPJ = ?1", nativeQuery = true)
	public List<PessoaJuridicaModel> pesquisaPorCnpj(String cnpj);
	
	@Query(value = "select * from PESSOAS_JURIDICAS where INSCRICAO_ESTADUAL = ?1", nativeQuery = true)
	public List<PessoaJuridicaModel> pesquisaPorInscEstadual(String inscricaoEstadual);
	
	@Query(value = "select * from PESSOAS_JURIDICAS where upper(trim(NOME)) like %?1%", nativeQuery = true)
	public List<PessoaJuridicaModel> pesquisaPorNome(String nome);
	
}
