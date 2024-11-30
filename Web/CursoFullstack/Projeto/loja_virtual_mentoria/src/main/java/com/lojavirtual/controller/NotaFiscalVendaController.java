package com.lojavirtual.controller;

import com.lojavirtual.model.NotaFiscalVendaModel;
import com.lojavirtual.repository.NotaFiscalVendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "**/notaFiscalVenda")
public class NotaFiscalVendaController {

	@Autowired
	private NotaFiscalVendaRepository notaFiscalVendaRepository;

	@GetMapping(value = "/{vendaId}")
	public ResponseEntity<List<NotaFiscalVendaModel>> buscar(@PathVariable Long vendaId) {
		List<NotaFiscalVendaModel> notasFiscaisVendas = notaFiscalVendaRepository.findByVendaId(vendaId);

		return new ResponseEntity<List<NotaFiscalVendaModel>>(notasFiscaisVendas, HttpStatus.OK);
	}


}
