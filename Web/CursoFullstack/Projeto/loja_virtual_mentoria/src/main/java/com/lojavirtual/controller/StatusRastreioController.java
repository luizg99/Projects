package com.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.StatusRastreioModel;
import com.lojavirtual.repository.StatusRastreioRepository;

@RestController
@RequestMapping(value = "/statusRastreio")
public class StatusRastreioController {

	@Autowired
	private StatusRastreioRepository statusRastreioRepository;
	

	@GetMapping(value = "/{vendaId}")
	public ResponseEntity<List<StatusRastreioModel>> buscar(@PathVariable Long vendaId) {
		List<StatusRastreioModel> status = statusRastreioRepository.findByVendaIdOrderById(vendaId);
		
		return new ResponseEntity<List<StatusRastreioModel>>(status, HttpStatus.OK);
	}

}
