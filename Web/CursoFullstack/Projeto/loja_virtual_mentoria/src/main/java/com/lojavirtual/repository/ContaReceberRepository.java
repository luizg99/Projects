package com.lojavirtual.repository;

import com.lojavirtual.model.ContaReceberModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface ContaReceberRepository extends JpaRepository<ContaReceberModel, Long> {
	
}
