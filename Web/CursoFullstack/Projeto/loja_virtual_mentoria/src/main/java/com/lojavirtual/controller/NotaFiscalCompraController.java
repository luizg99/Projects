package com.lojavirtual.controller;

import java.util.ArrayList;
import java.util.List;

import com.lojavirtual.model.dto.RelatorioComprasProdutosNotaFiscalDTO;
import com.lojavirtual.service.NotaFiscalCompraService;
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

import com.lojavirtual.model.NotaFiscalCompraModel;
import com.lojavirtual.repository.NotaFiscalCompraRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

@Controller
@RestController
public class NotaFiscalCompraController {

	@Autowired
	private NotaFiscalCompraRepository notaFiscalCompraRepository;

	@Autowired
	private NotaFiscalCompraService notaFiscalCompraService;
	
	@PostMapping(value = "**/salvarNotaFiscalCompra") 
	public ResponseEntity<NotaFiscalCompraModel> salvarNotaFiscalCompra(@RequestBody @Valid NotaFiscalCompraModel notaFiscalCompra) throws ExceptionMentoriaJava {
		if (notaFiscalCompra.getEmpresa() == null || notaFiscalCompra.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada.");
		}
		
		if (notaFiscalCompra.getPessoa() == null || notaFiscalCompra.getPessoa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Pessoa responsável deve ser informada.");
		}
		
		if (notaFiscalCompra.getContaPagar() == null || notaFiscalCompra.getContaPagar().getId() <= 0) {
			throw new ExceptionMentoriaJava("Conta a pagar deve ser informada.");
		}
		
		if (notaFiscalCompra.getId() == null) {
			if (notaFiscalCompra.getDescricaoObservacao() != null) { 
				List<NotaFiscalCompraModel> notasFiscaisCompra = notaFiscalCompraRepository.buscarNotaDescricao(notaFiscalCompra.getDescricaoObservacao().toUpperCase().trim());
			
				if (!notasFiscaisCompra.isEmpty()) {
					throw new ExceptionMentoriaJava("Já existe nota fiscal de compra com essa descrição.");
				}
			}
		}

		NotaFiscalCompraModel notaFiscalCompraSalva = notaFiscalCompraRepository.save(notaFiscalCompra);

		return new ResponseEntity<NotaFiscalCompraModel>(notaFiscalCompraSalva, HttpStatus.OK);
	}

	@GetMapping(value = "**/buscarNotaCompraPorDesc/{descricao}")
	public ResponseEntity<List<NotaFiscalCompraModel>> buscarNotaCompraPorDesc(@PathVariable String descricao) {
		List<NotaFiscalCompraModel> notaCompra = notaFiscalCompraRepository.buscarNotaDescricao(descricao.toUpperCase().trim());

		return new ResponseEntity<List<NotaFiscalCompraModel>>(notaCompra, HttpStatus.OK);
	}
	
	@GetMapping(value = "**/buscarNotaCompraPorId/{id}")
	public ResponseEntity<NotaFiscalCompraModel> buscarNotaCompraPorrId(@PathVariable Long id) throws ExceptionMentoriaJava {
		NotaFiscalCompraModel notaCompra = notaFiscalCompraRepository.findById(id).orElse(null);

		if (notaCompra == null) {
			throw new ExceptionMentoriaJava("Não existe nota fiscal de compra com o código " + id);
		}

		return new ResponseEntity<NotaFiscalCompraModel>(notaCompra, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "**/deleteNotaCompra/{id}")
	public ResponseEntity<String> deletNotaCompra(@PathVariable Long id) {
		notaFiscalCompraRepository.deleteItemNotaFiscalCompra(id);
		notaFiscalCompraRepository.deleteById(id);

		return new ResponseEntity<String>("Nota fiscal de compra removida.", HttpStatus.OK);
	}

	@PostMapping(value = "**/relatorioComprasProdutos")
	public ResponseEntity<List<RelatorioComprasProdutosNotaFiscalDTO>> relatorioComprasProdutos(@RequestBody RelatorioComprasProdutosNotaFiscalDTO relatorioDTO) {
		List<RelatorioComprasProdutosNotaFiscalDTO> retorno = new ArrayList<RelatorioComprasProdutosNotaFiscalDTO>();

		retorno = notaFiscalCompraService.gerarRelatorioComprasProdutos(relatorioDTO);

		return new ResponseEntity<List<RelatorioComprasProdutosNotaFiscalDTO>>(retorno, HttpStatus.OK);
	}


}
