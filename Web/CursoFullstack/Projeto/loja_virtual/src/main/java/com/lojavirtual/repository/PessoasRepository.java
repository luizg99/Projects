package com.lojavirtual.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.PessoasJuridicas;


//Definindo que é um repositório
@Repository
public interface PessoasRepository extends CrudRepository<PessoasJuridicas, Long>{
	
}
