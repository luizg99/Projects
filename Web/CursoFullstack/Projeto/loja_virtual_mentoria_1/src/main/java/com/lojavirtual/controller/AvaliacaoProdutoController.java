package com.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.AvaliacaoProdutoModel;
import com.lojavirtual.repository.AvaliacaoProdutoRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

@RestController
public class AvaliacaoProdutoController {
	
	@Autowired
	private AvaliacaoProdutoRepository avaliacaoProdutoRepository;
	
	@PostMapping(value = "**/salvarAvaliacaoProduto")  
	public ResponseEntity<AvaliacaoProdutoModel> salvarAvaliacaoProduto(@RequestBody @Valid AvaliacaoProdutoModel avaliacao) throws ExceptionMentoriaJava {
		if (avaliacao.getEmpresa() == null || avaliacao.getEmpresa() != null && avaliacao.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("É necessário informar a empresa da avaliação do produto.");
		}
		
		if (avaliacao.getProduto() == null || avaliacao.getProduto() != null && avaliacao.getProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("É necessário informar o produto da avaliação.");
		}
		
		if (avaliacao.getPessoa() == null || avaliacao.getPessoa() != null && avaliacao.getPessoa().getId() <= 0) {
			throw new ExceptionMentoriaJava("É necessário informar a pessoa ou cliente da avaliação.");
		}
		
		AvaliacaoProdutoModel avaliacaoSalva = avaliacaoProdutoRepository.saveAndFlush(avaliacao);
	
		return new ResponseEntity<AvaliacaoProdutoModel>(avaliacaoSalva, HttpStatus.OK);
	}
	
	
	@DeleteMapping(value = "**/deleteAvaliacao/{id}")  
	public ResponseEntity<String> deleteAvaliacao(@PathVariable Long id) {
		avaliacaoProdutoRepository.deleteById(id);
	
		return new ResponseEntity<String>("Avaliação removida.", HttpStatus.OK);
	}

	@GetMapping(value = "**/obterAvaliacaoProduto/{produtoId}")  
	public ResponseEntity<List<AvaliacaoProdutoModel>> obterAvaliacao(@PathVariable Long produtoId) {
		List<AvaliacaoProdutoModel> avaliacoes = avaliacaoProdutoRepository.avaliacoesProdutos(produtoId);
	
		return new ResponseEntity<List<AvaliacaoProdutoModel>>(avaliacoes, HttpStatus.OK);
	}
	
	@GetMapping(value = "**/obterAvaliacaoPessoa/{pessoaId}")  
	public ResponseEntity<List<AvaliacaoProdutoModel>> obterAvaliacaoPessoa(@PathVariable Long pessoaId) {
		List<AvaliacaoProdutoModel> avaliacoes = avaliacaoProdutoRepository.avaliacoesPessoa(pessoaId);
	
		return new ResponseEntity<List<AvaliacaoProdutoModel>>(avaliacoes, HttpStatus.OK);
	}
	
	@GetMapping(value = "**/obterAvaliacaoProdutoPessoa/{produtoId}/{pessoaId}")  
	public ResponseEntity<List<AvaliacaoProdutoModel>> obterAvaliacaoPessoa(@PathVariable Long produtoId, @PathVariable Long pessoaId) {
		List<AvaliacaoProdutoModel> avaliacoes = avaliacaoProdutoRepository.avaliacoesProdutosPessoa(produtoId, pessoaId);
	
		return new ResponseEntity<List<AvaliacaoProdutoModel>>(avaliacoes, HttpStatus.OK);
	}
}
