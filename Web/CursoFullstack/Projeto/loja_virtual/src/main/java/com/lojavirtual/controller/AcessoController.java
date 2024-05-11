package com.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.lojavirtual.model.Acessos;
import com.lojavirtual.service.AcessosService;

@Controller
public class AcessoController {

	@Autowired
	private AcessosService acessosService;
	
	@PostMapping("/salvarAcesso")
	public Acessos salvarAcesso(Acessos acessos) {
		return acessosService.save(acessos);
	}
}
