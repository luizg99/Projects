package com.lojavirtual.controller;

import com.lojavirtual.model.AvaliacaoProdutoModel;
import com.lojavirtual.model.CupomDescontoModel;
import com.lojavirtual.repository.AvaliacaoProdutoRepository;
import com.lojavirtual.repository.CupomDescontoRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "**/cupomDesconto")
public class CupomDescontoController {
	
	@Autowired
	private CupomDescontoRepository cupomDescontoRepository;

	@GetMapping(value = "/{empresaId}")
	public ResponseEntity<List<CupomDescontoModel>> buscar(@PathVariable Long empresaId) {
		return new ResponseEntity<List<CupomDescontoModel>>(cupomDescontoRepository.findByEmpresaId(empresaId), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<CupomDescontoModel> salvar(@Valid @RequestBody CupomDescontoModel cupomDescontoModel) {
		CupomDescontoModel cupomSalvo = cupomDescontoRepository.save(cupomDescontoModel);
		return new ResponseEntity<>(cupomSalvo, HttpStatus.CREATED);
	}

}
