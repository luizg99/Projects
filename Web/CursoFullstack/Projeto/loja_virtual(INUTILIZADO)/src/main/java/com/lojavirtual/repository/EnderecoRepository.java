package com.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.Enderecos;

@Repository
public interface EnderecoRepository extends JpaRepository<Enderecos, Long>{

}
