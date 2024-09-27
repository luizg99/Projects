package com.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.EnderecoModel;

@Repository
@Transactional
public interface EnderecoRepository extends JpaRepository<EnderecoModel, Long> {
	
}
