package com.lojavirtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lojavirtual.model.Produtos;
import com.lojavirtual.repository.ProdutoRepository;

@Service
public class ProdutosService {
	
	@Autowired
	private ProdutoRepository produtosRepository;

	public Produtos save(Produtos produtos) {
		return produtosRepository.save(produtos);
	}
}
