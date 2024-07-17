package com.lojavirtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lojavirtual.model.Acessos;
import com.lojavirtual.repository.AcessoRepository;

@Service
public class AcessosService {
	
	@Autowired
	private AcessoRepository acessosRepository;

	public Acessos save(Acessos acessos) {
		return acessosRepository.save(acessos);
	}
}
