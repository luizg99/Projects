package com.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.controller.dto.CategoriaProdutoDTO;
import com.lojavirtual.model.Acessos;
import com.lojavirtual.model.CategoriaProduto;
import com.lojavirtual.repository.CategoriaProdutoRepository;

import util.ExceptionMentoriaJava;

@RestController
public class CategoriaProdutoController {
	
	@Autowired
	private CategoriaProdutoRepository categoriaProdutoRepository;
	
	@ResponseBody
	@PostMapping(value = "**/salvarCategoriaProduto")
	public ResponseEntity<CategoriaProdutoDTO> salvarOuAtualizarCategoria(@RequestBody CategoriaProduto categoriaProduto) throws ExceptionMentoriaJava{
		
		if (categoriaProduto.getEmpresa() == null || categoriaProduto.getEmpresa().getId() == null){
			throw new ExceptionMentoriaJava("A empresa deve ser informada");
			
		}
		
		if (categoriaProduto.getId() == null && categoriaProdutoRepository.existeCategoria(categoriaProduto.getDescricao())) {
			throw new ExceptionMentoriaJava("Não pode cadastrar categoria com o mesmo nome");
		}
		
		CategoriaProduto categoriaSalva = categoriaProdutoRepository.save(categoriaProduto);
		
		CategoriaProdutoDTO categoriaProdutoDTO = new CategoriaProdutoDTO();
		categoriaProdutoDTO.setId(categoriaSalva.getId());
		categoriaProdutoDTO.setDescricao(categoriaSalva.getDescricao());
		categoriaProdutoDTO.setEmpresa(categoriaSalva.getEmpresa().getId().toString());
		
		return new ResponseEntity<CategoriaProdutoDTO>(categoriaProdutoDTO, HttpStatus.OK);
	}
	
	@PostMapping(value = "**/deleteCategoriaProduto")  
	public ResponseEntity<?> deleteCategoriaProduto(@RequestBody CategoriaProduto categoriaProduto) {
		categoriaProdutoRepository.deleteById(categoriaProduto.getId());
		
		return new ResponseEntity("Categoria removida.", HttpStatus.OK);
	}
	
	
}