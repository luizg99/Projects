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

import com.lojavirtual.model.CategoriaProdutoModel;
import com.lojavirtual.model.dto.CategoriaProdutoDTO;
import com.lojavirtual.repository.CategoriaProdutoRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

@Controller
@RestController
public class CategoriaProdutoController {

	@Autowired
	private CategoriaProdutoRepository categoriaProdutoRepository;
	
	@PostMapping(value = "**/salvarCategoria")  // ** -> SERVE PRA IGNORAR O RESTANTE DA URL
	public ResponseEntity<CategoriaProdutoDTO> salvarCategoria(@RequestBody CategoriaProdutoModel categoria) throws ExceptionMentoriaJava {
		if (categoria.getEmpresa() == null || categoria.getEmpresa().getId() == null ) {
			throw new ExceptionMentoriaJava("A empresa deve ser informada.");
		}
		
		if (categoria.getId() == null && categoriaProdutoRepository.existeCategoria(categoria.getDescricao().toUpperCase())) {
			throw new ExceptionMentoriaJava("Categoria do produto já cadastrada, verifique.");
		}
		
		CategoriaProdutoModel categoriaProduto = categoriaProdutoRepository.save(categoria);
		
		CategoriaProdutoDTO categoriaProdutoDto = new CategoriaProdutoDTO();
		categoriaProdutoDto.setId(categoriaProduto.getId());
		categoriaProdutoDto.setDescricao(categoriaProduto.getDescricao());
		categoriaProdutoDto.setEmpresa(categoriaProduto.getEmpresa().getId().toString());
		
		return new ResponseEntity<CategoriaProdutoDTO>(categoriaProdutoDto, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "**/deleteCategoriaProduto/{id}")  
	public ResponseEntity<String> deleteCategoriaProdutoPorId(@PathVariable Long id) {
		if (categoriaProdutoRepository.findById(id).isPresent() == false) {
			return new ResponseEntity<String>("Categoria já foi removida.", HttpStatus.OK);
		}
		
		categoriaProdutoRepository.deleteById(id);
	
		return new ResponseEntity<String>("Categoria removida.", HttpStatus.OK);
	}
	
	@GetMapping(value = "**/buscarCategoriaPorDesc/{descricao}")  
	public ResponseEntity<List<CategoriaProdutoModel>> buscaCategoriaPorDesc(@PathVariable String descricao) {
		List<CategoriaProdutoModel> categorias = categoriaProdutoRepository.buscarCategoriaDescricao(descricao.trim().toUpperCase());
	
		return new ResponseEntity<List<CategoriaProdutoModel>>(categorias, HttpStatus.OK);
	}

}
