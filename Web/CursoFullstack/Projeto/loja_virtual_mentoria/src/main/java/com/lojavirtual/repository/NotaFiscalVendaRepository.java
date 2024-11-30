package com.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.NotaFiscalVendaModel;

import java.util.List;

@Repository
@Transactional
public interface NotaFiscalVendaRepository extends JpaRepository<NotaFiscalVendaModel, Long> {

    List<NotaFiscalVendaModel> findByVendaId(Long vendaId);
}
