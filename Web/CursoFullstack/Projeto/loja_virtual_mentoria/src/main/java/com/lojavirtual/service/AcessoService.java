package com.lojavirtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lojavirtual.model.AcessoModel;
import com.lojavirtual.repository.AcessoRepository;

@Service
public class AcessoService {

	@Autowired
	private AcessoRepository acessoRepository;
	
	public AcessoModel save(AcessoModel acesso) {
		return acessoRepository.save(acesso);
	}
}
