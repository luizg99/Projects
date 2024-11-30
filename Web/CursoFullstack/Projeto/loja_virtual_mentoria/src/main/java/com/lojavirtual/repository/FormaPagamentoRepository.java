package com.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lojavirtual.model.FormaPagamentoModel;

import java.util.List;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamentoModel, Long> {

    List<FormaPagamentoModel> findByEmpresaId(Long empresaId);
} 
