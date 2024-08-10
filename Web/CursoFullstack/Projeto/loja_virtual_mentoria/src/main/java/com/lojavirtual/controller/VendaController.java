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

import com.lojavirtual.model.EnderecoModel;
import com.lojavirtual.model.PessoaFisicaModel;
import com.lojavirtual.model.StatusRastreioModel;
import com.lojavirtual.model.VendaItemModel;
import com.lojavirtual.model.VendaModel;
import com.lojavirtual.model.dto.VendaDTO;
import com.lojavirtual.model.dto.VendaItemDTO;
import com.lojavirtual.repository.EnderecoRepository;
import com.lojavirtual.repository.NotaFiscalVendaRepository;
import com.lojavirtual.repository.StatusRastreioRepository;
import com.lojavirtual.repository.VendaRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "**/venda")
public class VendaController {
	
	@Autowired
	private VendaRepository vendaRepository;
	
	@Autowired
	private PessoaController pessoaController;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private NotaFiscalVendaRepository notaFiscalVendaRepository;

	@Autowired
	private StatusRastreioRepository statusRastreioRepository;

	@PostMapping  
	public ResponseEntity<VendaDTO> salvarVenda(@RequestBody @Valid VendaModel venda) throws ExceptionMentoriaJava {
		if (venda.getPessoa().getId() <= 0) {
			venda.getPessoa().setEmpresa(venda.getEmpresa());
		
			PessoaFisicaModel cliente = pessoaController.salvarPessoaFisica(venda.getPessoa()).getBody();
			venda.setPessoa(cliente);
		}

		if (venda.getEnderecoCobranca().getId() <= 0) {
			EnderecoModel enderecoCobranca = venda.getEnderecoCobranca();
			enderecoCobranca = enderecoRepository.save(enderecoCobranca);
			venda.setEnderecoCobranca(enderecoCobranca);
		}

		if (venda.getEnderecoEntrega().getId() <= 0) {
			EnderecoModel enderecoEntrega = venda.getEnderecoEntrega();
			enderecoEntrega = enderecoRepository.save(enderecoEntrega);
			venda.setEnderecoEntrega(enderecoEntrega);
		}

		venda.getNotaFiscalVenda().setEmpresa(venda.getEmpresa());

		for (int i = 0; i < venda.getItens().size(); i++) {
			venda.getItens().get(i).setEmpresa(venda.getEmpresa());
			venda.getItens().get(i).setVenda(venda);
		}

		venda = vendaRepository.saveAndFlush(venda);

		StatusRastreioModel statusRastreio = new StatusRastreioModel();
		statusRastreio.setCentroDistribuicao("CD");
		statusRastreio.setCidade("CD");
		statusRastreio.setEmpresa(venda.getEmpresa());
		statusRastreio.setEstado("GO");
		statusRastreio.setVenda(venda);

		statusRastreioRepository.save(statusRastreio);

		venda.getNotaFiscalVenda().setVenda(venda);

		notaFiscalVendaRepository.saveAndFlush(venda.getNotaFiscalVenda());

		VendaDTO vendaDto = new VendaDTO();

		vendaDto.setId(venda.getId());
		vendaDto.setValorTotal(venda.getValorTotal());
		vendaDto.setPesssoa(venda.getPessoa());
		vendaDto.setEnderecoCobranca(venda.getEnderecoCobranca());
		vendaDto.setEnderecoEntrega(venda.getEnderecoEntrega());
		vendaDto.setValorDesconto(venda.getValorDesconto());
		vendaDto.setValorFrete(venda.getValorFrete());

		for (VendaItemModel item : venda.getItens()) {
			VendaItemDTO itemDto = new VendaItemDTO();
			itemDto.setProduto(item.getProduto());
			itemDto.setQuantidade(item.getQuantidade());

			vendaDto.getItens().add(itemDto);
		}

		return new ResponseEntity<VendaDTO>(vendaDto, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<VendaDTO> buscar(@PathVariable Long id) {
		VendaModel venda = vendaRepository.findById(id).orElse(null);
		VendaDTO vendaDto = new VendaDTO();

		vendaDto.setId(venda.getId());
		vendaDto.setValorTotal(venda.getValorTotal());
		vendaDto.setPesssoa(venda.getPessoa());
		vendaDto.setEnderecoCobranca(venda.getEnderecoCobranca());
		vendaDto.setEnderecoEntrega(venda.getEnderecoEntrega());
		vendaDto.setValorDesconto(venda.getValorDesconto());
		vendaDto.setValorFrete(venda.getValorFrete());

		for (VendaItemModel item : venda.getItens()) {
			VendaItemDTO itemDto = new VendaItemDTO();
			itemDto.setProduto(item.getProduto());
			itemDto.setQuantidade(item.getQuantidade());

			vendaDto.getItens().add(itemDto);
		}

		return new ResponseEntity<VendaDTO>(vendaDto, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deletar(@PathVariable Long id) {
		vendaRepository.deletarVenda(id);
		return new ResponseEntity<String>("Venda excluída com sucesso.", HttpStatus.OK);
	}

}
