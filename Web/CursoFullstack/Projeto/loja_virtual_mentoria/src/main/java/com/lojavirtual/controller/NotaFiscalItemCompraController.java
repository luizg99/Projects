package com.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.NotaItemCompraModel;
import com.lojavirtual.repository.NotaFiscalItemCompraRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

@Controller
@RestController
public class NotaFiscalItemCompraController {

	@Autowired
	private NotaFiscalItemCompraRepository notaFiscalItemCompraRepository;
	
	@PostMapping(value = "**/salvarNotaFiscalItemCompra") 
	public ResponseEntity<NotaItemCompraModel> salvarNotaFiscalCompra(@RequestBody @Valid NotaItemCompraModel notaItemCompra) throws ExceptionMentoriaJava {
		if (notaItemCompra.getEmpresa() == null || notaItemCompra.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada.");
		}
		
		if (notaItemCompra.getProduto() == null || notaItemCompra.getProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("Produto deve ser informado.");
		}
		
		if (notaItemCompra.getNotaFiscalCompra() == null || notaItemCompra.getNotaFiscalCompra().getId() <= 0) {
			throw new ExceptionMentoriaJava("A nota fiscal de compra deve ser informada.");
		}
		
		if (notaItemCompra.getId() == null) {
			List<NotaItemCompraModel> notasFiscaisItensCompra = notaFiscalItemCompraRepository.buscarNotaItemPorProdutoNota(notaItemCompra.getProduto().getId(), notaItemCompra.getNotaFiscalCompra().getId());
		
			if (!notasFiscaisItensCompra.isEmpty()) {
				throw new ExceptionMentoriaJava("Já existe esse produto cadastrado para essa nota.");
			}
			
		}
		
		if (notaItemCompra.getQuantidade() <= 0) {
			throw new ExceptionMentoriaJava("A quantidade do produto deve ser informada.");
		}

		NotaItemCompraModel notaItemCompraSalva = notaFiscalItemCompraRepository.save(notaItemCompra);

		return new ResponseEntity<NotaItemCompraModel>(notaItemCompraSalva, HttpStatus.OK);
	}

	@DeleteMapping(value = "**/deleteNotaItemCompra/{id}")
	public ResponseEntity<String> deleteNotaItemCompra(@PathVariable Long id) {
		notaFiscalItemCompraRepository.deleteById(id);

		return new ResponseEntity<String>("Item da nota fiscal de compra removido.", HttpStatus.OK);
	}

}
