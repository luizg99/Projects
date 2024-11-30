package com.lojavirtual.repository;

import com.lojavirtual.model.CupomDescontoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CupomDescontoRepository extends JpaRepository<CupomDescontoModel, Long> {

	List<CupomDescontoModel> findByEmpresaId(Long empresaId);
}
