package com.lojavirtual.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.PessoasJuridicas;
import com.lojavirtual.repository.PessoasRepository;

import util.ExceptionMentoriaJava;

@RestController
public class PessoaController {
	
	@ResponseBody
	@PostMapping(value = "**/salvarPj")
	public ResponseEntity<PessoasJuridicas> salvarPj(@RequestBody PessoasJuridicas pessoaJuridica) throws ExceptionMentoriaJava {
		
		if (pessoaJuridica == null) {
			throw new ExceptionMentoriaJava("Pessoa juridica não pode ser vazio.");
		}
		
		//if (pessoaJuridica.getId() == null && PessoasRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
		//	throw new ExceptionMentoriaJava("Já existe pessoa já cadastrada com o seguinte CNPJ: " + pessoaJuridica.getCnpj());
		//}
		
		//pessoaJuridica = pessoaUsuarioService.salvarPessoaJuridica(pessoaJuridica);
		return new ResponseEntity<PessoasJuridicas>(pessoaJuridica, HttpStatus.OK);
	}
}
