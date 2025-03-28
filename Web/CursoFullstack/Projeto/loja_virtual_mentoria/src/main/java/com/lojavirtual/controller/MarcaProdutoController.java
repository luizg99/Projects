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

import com.lojavirtual.model.MarcaProdutoModel;
import com.lojavirtual.repository.MarcaProdutoRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

@Controller
@RestController
public class MarcaProdutoController {

	@Autowired
	private MarcaProdutoRepository marcaProdutoRepository;

	@PostMapping(value = "**/salvarMarcaProduto") 
	public ResponseEntity<MarcaProdutoModel> salvarMarcaProduto(@RequestBody @Valid MarcaProdutoModel marcaProduto) throws ExceptionMentoriaJava {
		if (marcaProduto.getEmpresa() == null || marcaProduto.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada.");
		}
		
		if (marcaProduto.getId() == null) {
			List<MarcaProdutoModel> marcasProdutos = marcaProdutoRepository.buscarMarcaProdutoDescricao(marcaProduto.getDescricao().toUpperCase().trim(), marcaProduto.getEmpresa().getId());

			if (!marcasProdutos.isEmpty()) {
				throw new ExceptionMentoriaJava("Já existe marca do produto com esse nome.");
			}
		}

		MarcaProdutoModel marcaProdutoSalvo = marcaProdutoRepository.save(marcaProduto);

		return new ResponseEntity<MarcaProdutoModel>(marcaProdutoSalvo, HttpStatus.OK);
	}

	@PostMapping(value = "**/deleteMarcaProduto")
	public ResponseEntity<String> deleteMarcaProduto(@RequestBody MarcaProdutoModel produto) {
		marcaProdutoRepository.deleteById(produto.getId());

		return new ResponseEntity<String>("Marca removida.", HttpStatus.OK);
	}

	@DeleteMapping(value = "**/deleteMarcaProduto/{id}")
	public ResponseEntity<String> deleteMarcaProduto(@PathVariable Long id) {
		marcaProdutoRepository.deleteById(id);

		return new ResponseEntity<String>("Marca removida.", HttpStatus.OK);
	}

	@GetMapping(value = "**/	/{id}")
	public ResponseEntity<MarcaProdutoModel> buscarMarcaProdutoPorId(@PathVariable Long id) throws ExceptionMentoriaJava {
		MarcaProdutoModel marcasProduto = marcaProdutoRepository.findById(id).orElse(null);

		if (marcasProduto == null) {
			throw new ExceptionMentoriaJava("Não existe marca cadastrado com o código " + id);
		}

		return new ResponseEntity<MarcaProdutoModel>(marcasProduto, HttpStatus.OK);
	}

	@GetMapping(value = "**/buscarMarcaProdutoPorDesc/{descricao}")
	public ResponseEntity<List<MarcaProdutoModel>> buscarMarcaProdutoPorDesc(@PathVariable String descricao) {
		List<MarcaProdutoModel> marcasProdutos = marcaProdutoRepository.buscarMarcaProdutoDescricao(descricao.toUpperCase().trim());

		return new ResponseEntity<List<MarcaProdutoModel>>(marcasProdutos, HttpStatus.OK);
	}
}
