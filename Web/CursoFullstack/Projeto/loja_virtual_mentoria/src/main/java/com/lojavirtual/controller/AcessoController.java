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

import com.lojavirtual.model.AcessoModel;
import com.lojavirtual.repository.AcessoRepository;
import com.lojavirtual.service.AcessoService;

@Controller
@RestController
public class AcessoController {

	@Autowired
	private AcessoService acessoService;
	
	@Autowired
	private AcessoRepository acessoRepository;
	
	@PostMapping(value = "**/salvarAcesso")  // ** -> SERVE PRA IGNORAR O RESTANTE DA URL
	public ResponseEntity<AcessoModel> salvarAcesso(@RequestBody AcessoModel acesso) {
		AcessoModel acessoSalvo = acessoService.save(acesso);
	
		return new ResponseEntity<AcessoModel>(acessoSalvo, HttpStatus.OK);
	}
	
	@PostMapping(value = "**/deleteAcesso")  
	public ResponseEntity<String> deleteAcesso(@RequestBody AcessoModel acesso) {
		acessoRepository.deleteById(acesso.getId());
	
		return new ResponseEntity<String>("Acesso removido.", HttpStatus.OK);
	}
	
	//@Secured({"ROLE_GERENTE", "ROLE_ADMIN"})
	@DeleteMapping(value = "**/deleteAcessoPorId/{id}")  
	public ResponseEntity<?> deleteAcessoPorId(@PathVariable Long id) {
		acessoRepository.deleteById(id);
	
		return new ResponseEntity<String>("Acesso removido.", HttpStatus.OK);
	}

	@GetMapping(value = "**/obterAcessoPorId/{id}")  
	public ResponseEntity<AcessoModel> obterAcessoPorId(@PathVariable Long id) {
		AcessoModel acesso = acessoRepository.findById(id).get();
	
		return new ResponseEntity<AcessoModel>(acesso, HttpStatus.OK);
	}
	
	@GetMapping(value = "**/buscarAcessoPorDesc/{descricao}")  
	public ResponseEntity<List<AcessoModel>> buscarAcessoPorDesc(@PathVariable String descricao) {
		List<AcessoModel> acessos = acessoRepository.buscarAcessoDescricao(descricao);
	
		return new ResponseEntity<List<AcessoModel>>(acessos, HttpStatus.OK);
	}
}
