package com.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
	private AcessosService acessosService;
	
	@PostMapping(value = "**/salvarAcesso")  // ** -> SERVE PRA IGNORAR O RESTANTE DA URL
	public ResponseEntity<Acessos> salvarAcesso(@RequestBody Acessos acesso) {
		Acessos acessoSalvo = acessosService.save(acesso);
	
		return new ResponseEntity<Acessos>(acessoSalvo, HttpStatus.OK);
	}	
}
