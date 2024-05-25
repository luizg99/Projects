package com.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.Acessos;
import com.lojavirtual.repository.AcessosRepository;
import com.lojavirtual.service.AcessosService;

@Controller
@RestController
public class AcessoController {

	@Autowired
	private AcessosService acessoService;
	
	@Autowired
	private AcessosRepository acessoRepository;
	
	@PostMapping(value = "**/salvarAcesso")  // ** -> SERVE PRA IGNORAR O RESTANTE DA URL
	public ResponseEntity<Acessos> salvarAcesso(@RequestBody Acessos acesso) {
		Acessos acessoSalvo = acessoService.save(acesso);
	
		return new ResponseEntity<Acessos>(acessoSalvo, HttpStatus.OK);
	}	
	
	@PreAuthorize("hasAuthority('SCOPE_ACESSO_ADMIN')")
	@PostMapping(value = "**/deleteAcesso")  
	public ResponseEntity<?> deleteAcesso(@RequestBody Acessos acesso) {
		acessoRepository.deleteById(acesso.getId());
		
		return new ResponseEntity("Acesso removido.", HttpStatus.OK);
	}
	
	@DeleteMapping(value = "**/deleteAcessoPorId/{id}")  
	public ResponseEntity<?> deleteAcessoPorId(@PathVariable Long id) {
		acessoRepository.deleteById(id);
	
		return new ResponseEntity("Acesso removido.", HttpStatus.OK);
	}
	
	@GetMapping(value = "**/obterAcessoPorId/{id}")  
	public ResponseEntity<Acessos> obterAcessoPorId(@PathVariable Long id) {
		Acessos acesso = acessoRepository.findById(id).get();
	
		return new ResponseEntity<Acessos>(acesso, HttpStatus.OK);
	}
	
	@GetMapping(value = "**/buscarAcessoPorDesc/{descricao}")  
	public ResponseEntity<List<Acessos>> buscarAcessoPorDesc(@PathVariable String descricao) {
		List<Acessos> acessos = acessoRepository.buscarAcessoDescricao(descricao);
	
		return new ResponseEntity<List<Acessos>>(acessos, HttpStatus.OK);
	}
}
