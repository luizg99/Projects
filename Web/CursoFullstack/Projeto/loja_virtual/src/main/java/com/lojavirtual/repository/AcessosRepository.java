package com.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.Acessos;

import jakarta.transaction.Transactional;

//Definindo que é um repositório
@Repository
//Definindo para o repositório gerenciar as transações com o banco de dados.
@Transactional
public interface AcessosRepository extends JpaRepository<Acessos, Integer>{

}
