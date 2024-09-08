package com.lojavirtual.controller;

import java.util.List;

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

import com.lojavirtual.model.ContaPagarModel;
import com.lojavirtual.model.ProdutoModel;
import com.lojavirtual.repository.ContaPagarRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

@Controller
@RestController
public class ContaPagarController {

	@Autowired
	private ContaPagarRepository contaPagarRepository;

	@PostMapping(value = "**/salvarContaPagar") 
	public ResponseEntity<ContaPagarModel> salvarContaPagar(@RequestBody @Valid ContaPagarModel contaPagar) throws ExceptionMentoriaJava {
		if (contaPagar.getEmpresa() == null || contaPagar.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada.");
		}
		
		if (contaPagar.getPessoa() == null || contaPagar.getPessoa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Pessoa responsável deve ser informada.");
		}
		
		if (contaPagar.getFornecedor() == null || contaPagar.getFornecedor().getId() <= 0) {
			throw new ExceptionMentoriaJava("Fornecedor responsável deve ser informada.");
		}
		
		if (contaPagar.getId() == null) {
			List<ContaPagarModel> contasPagar = contaPagarRepository.buscarContasPagarDesc(contaPagar.getDescricao().toUpperCase());

			if (!contasPagar.isEmpty()) {
				throw new ExceptionMentoriaJava("Já existe conta a pagar com esse nome.");
			}
		}

		ContaPagarModel contasPagarSalva = contaPagarRepository.save(contaPagar);

		return new ResponseEntity<ContaPagarModel>(contasPagarSalva, HttpStatus.OK);
	}

	@PostMapping(value = "**/deleteContaPagar")
	public ResponseEntity<String> deleteContaPagar(@RequestBody ProdutoModel produto) {
		contaPagarRepository.deleteById(produto.getId());

		return new ResponseEntity<String>("Conta pagar removida.", HttpStatus.OK);
	}

	@DeleteMapping(value = "**/deleteContaPagarPorId/{id}")
	public ResponseEntity<String> deleteContaPagarPorId(@PathVariable Long id) {
		contaPagarRepository.deleteById(id);

		return new ResponseEntity<String>("Conta pagar removida.", HttpStatus.OK);
	}

	@GetMapping(value = "**/buscarContaPagarPorId/{id}")
	public ResponseEntity<ContaPagarModel> buscarContaPagarPorId(@PathVariable Long id) throws ExceptionMentoriaJava {
		ContaPagarModel contaPagar = contaPagarRepository.findById(id).orElse(null);

		if (contaPagar == null) {
			throw new ExceptionMentoriaJava("Não existe conta a pagar cadastrada com esse código." + id);
		}

		return new ResponseEntity<ContaPagarModel>(contaPagar, HttpStatus.OK);
	}

	@GetMapping(value = "**/buscarContasPagarDesc/{descricao}")
	public ResponseEntity<List<ContaPagarModel>> buscarContasPagarDesc(@PathVariable String descricao) {
		List<ContaPagarModel> contasPagar = contaPagarRepository.buscarContasPagarDesc(descricao.toUpperCase().trim());

		return new ResponseEntity<List<ContaPagarModel>>(contasPagar, HttpStatus.OK);
	}
}
