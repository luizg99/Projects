package com.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.PessoaFisicaModel;
import com.lojavirtual.model.PessoaJuridicaModel;
import com.lojavirtual.repository.PessoaFisicaRepository;
import com.lojavirtual.repository.PessoaJuridicaRepository;
import com.lojavirtual.service.PessoaUsuarioService;

import util.ExceptionMentoriaJava;

@RestController
public class PessoaController {
	@Autowired
	private PessoaJuridicaRepository pessoaJuridicaRepository;
	
	@Autowired 
	private PessoaUsuarioService pessoaUsuarioService;
	
	//@ResponseBod
	@PostMapping(value = "**/salvarPj")
	public ResponseEntity<PessoaJuridicaModel> salvarPj(@RequestBody PessoaJuridicaModel pessoaJuridica) throws ExceptionMentoriaJava {
		
		if (pessoaJuridica == null) {
			throw new ExceptionMentoriaJava("Pessoa juridica não pode ser vazio.");
		}
		
		if (pessoaJuridica.getId() == null && pessoaJuridicaRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
			throw new ExceptionMentoriaJava("Já existe pessoa já cadastrada com o seguinte CNPJ: " + pessoaJuridica.getCnpj());
		}
		
		pessoaJuridica = pessoaUsuarioService.salvarPessoaJuridica(pessoaJuridica);
		return new ResponseEntity<PessoaJuridicaModel>(pessoaJuridica, HttpStatus.OK);
	}
}
