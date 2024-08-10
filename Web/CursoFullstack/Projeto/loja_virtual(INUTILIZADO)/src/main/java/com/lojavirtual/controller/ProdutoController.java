package com.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.Produtos;

import com.lojavirtual.repository.ProdutoRepository;
import com.lojavirtual.service.ProdutosService;

import jakarta.validation.Valid;
import util.ExceptionMentoriaJava;

@Controller
@RestController
public class ProdutoController {

	@Autowired
	private ProdutosService produtosService;
	
	@Autowired
	private ProdutoRepository produtosRepository;
	
	@PostMapping(value = "**/salvarProduto")  // ** -> SERVE PRA IGNORAR O RESTANTE DA URL
	public ResponseEntity<Produtos> salvarProduto(@RequestBody Produtos produto) throws ExceptionMentoriaJava {
		if (produto.getEmpresa() == null || produto.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada.");
		}
		
		if (produto.getId() == null) {
			List<Produtos> produtos = produtosRepository.buscarProdutoNome(produto.getDescricao().toUpperCase(), produto.getEmpresa().getId());

			if (!produtos.isEmpty()) {
				throw new ExceptionMentoriaJava("Já existe produto com esse nome.");
			}
		}
		
		Produtos ProdutoSalvo = produtosService.save(produto);
	
		return new ResponseEntity<Produtos>(ProdutoSalvo, HttpStatus.OK);
	}	
	
	@PostMapping(value = "**/deleteProduto")
	public ResponseEntity<String> deleteAcesso(@RequestBody Produtos produto) {
		produtosRepository.deleteById(produto.getId());

		return new ResponseEntity<String>("Produto removido.", HttpStatus.OK);
	}

	@DeleteMapping(value = "**/deleteProdutoPorId/{id}")
	public ResponseEntity<String> deleteProdutoPorId(@PathVariable Long id) {
		produtosRepository.deleteById(id);

		return new ResponseEntity<String>("Produto removido.", HttpStatus.OK);
	}

	@GetMapping(value = "**/obterProdutoPorId/{id}")
	public ResponseEntity<Produtos> obterProdutoPorId(@PathVariable Long id) throws ExceptionMentoriaJava {
		Produtos produto = produtosRepository.findById(id).orElse(null);

		if (produto == null) {
			throw new ExceptionMentoriaJava("Não existe produto cadastrado com esse código." + id);
		}

		return new ResponseEntity<Produtos>(produto, HttpStatus.OK);
	}

	@GetMapping(value = "**/buscarProdutoPorNome/{nome}")
	public ResponseEntity<List<Produtos>> buscarProdutoPorNome(@PathVariable String nome) {
		List<Produtos> produtos = produtosRepository.buscarProdutoNome(nome);

		return new ResponseEntity<List<Produtos>>(produtos, HttpStatus.OK);
	}
}
