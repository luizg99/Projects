package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.Acessos;

import jakarta.transaction.Transactional;

//Definindo que é um repositório
@Repository
//Definindo para o repositório gerenciar as transações com o banco de dados.
@Transactional
public interface AcessoRepository extends JpaRepository<Acessos, Long>{
	@Query(value = "select * from acessos a where upper(trim(a.DESCRICAO)) like %?1%", nativeQuery = true)
	List<Acessos> buscarAcessoDescricao(String descricao);
}
