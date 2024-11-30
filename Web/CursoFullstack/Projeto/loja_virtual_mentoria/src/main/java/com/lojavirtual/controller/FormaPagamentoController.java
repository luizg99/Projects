package com.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.FormaPagamentoModel;
import com.lojavirtual.repository.FormaPagamentoRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("**/formaPagamento")
public class FormaPagamentoController {

	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;
	
	@PostMapping  
	public ResponseEntity<FormaPagamentoModel> salvar(@RequestBody @Valid FormaPagamentoModel formaPagamento) throws ExceptionMentoriaJava {
		FormaPagamentoModel formaPagamentoSalvo = formaPagamentoRepository.save(formaPagamento);
		
		return new ResponseEntity<FormaPagamentoModel>(formaPagamentoSalvo, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}")  
	public ResponseEntity<String> deletar(@PathVariable Long id) {
		if (!formaPagamentoRepository.existsById(id)) {
			return new ResponseEntity<String>("A forma de pagamento já foi removida.", HttpStatus.OK);
		}
		
		formaPagamentoRepository.deleteById(id);
	
		return new ResponseEntity<String>("Formas de pagamento removida.", HttpStatus.OK);
	}
	
	@GetMapping(value = "/{empresaId}")
	public ResponseEntity<List<FormaPagamentoModel>> buscar(@PathVariable Long empresaId) {
		List<FormaPagamentoModel> formasPagamentos = formaPagamentoRepository.findByEmpresaId(empresaId);

		return new ResponseEntity<List<FormaPagamentoModel>>(formasPagamentos, HttpStatus.OK);
	}

}
