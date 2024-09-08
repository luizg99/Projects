package com.lojavirtual.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.lojavirtual.enums.StatusContaReceber;
import com.lojavirtual.model.*;
import com.lojavirtual.repository.*;
import com.lojavirtual.service.EnvioEmailService;
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

import com.lojavirtual.model.dto.VendaDTO;
import com.lojavirtual.model.dto.VendaItemDTO;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

import javax.mail.MessagingException;

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

	@Autowired
	private ContaReceberRepository contaReceberRepository;

	@Autowired
	private EnvioEmailService envioEmailService;

	@PostMapping  
	public ResponseEntity<VendaDTO> salvarVenda(@RequestBody @Valid VendaModel venda) throws ExceptionMentoriaJava, MessagingException, UnsupportedEncodingException {
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

		ContaReceberModel contaReceberModel = new ContaReceberModel();

		contaReceberModel.setDescricao("Venda da loja virtual n- " + venda.getId());
		contaReceberModel.setDataPagamento(Calendar.getInstance().getTime());
		contaReceberModel.setDataVencimento(Calendar.getInstance().getTime());
		contaReceberModel.setEmpresa(venda.getEmpresa());
		contaReceberModel.setPessoa(venda.getPessoa());
		contaReceberModel.setStatus(StatusContaReceber.QUITADA);
		contaReceberModel.setValorDesconto(venda.getValorDesconto());
		contaReceberModel.setValorTotal(venda.getValorTotal());

		contaReceberRepository.saveAndFlush(contaReceberModel);

		StringBuilder mensagemEmail = new StringBuilder();
		mensagemEmail.append("Olá, ").append(venda.getPessoa().getNome()).append("</br>");
		mensagemEmail.append("Você realizou a compra de n: ").append(venda.getId()).append("</br>");
		mensagemEmail.append("Na loja ").append(venda.getEmpresa().getNomeFantasia()).append("</br>");

		envioEmailService.enviarEmailHtml("Compra realizada", mensagemEmail.toString(), venda.getPessoa().getEmail());

		mensagemEmail = new StringBuilder();
		mensagemEmail.append("Você realizou a venda de n: ").append(venda.getId()).append("</br>");
		envioEmailService.enviarEmailHtml("Venda realizada", mensagemEmail.toString(), venda.getEmpresa().getEmail());

		return new ResponseEntity<VendaDTO>(vendaDto, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<VendaDTO> buscar(@PathVariable Long id) {
		VendaModel venda = vendaRepository.findByIdExclusao(id).orElse(new VendaModel());
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

	@DeleteMapping(value = "/logica/{id}")
	public ResponseEntity<String> deleteLogico(@PathVariable Long id) {
		vendaRepository.mudarStatusVenda(id, true);
		return new ResponseEntity<String>("Venda excluída com sucesso.", HttpStatus.OK);
	}

	@PostMapping(value = "/logica/{id}")
	public ResponseEntity<String> ativarVenda(@PathVariable Long id) {
		vendaRepository.mudarStatusVenda(id, false);
		return new ResponseEntity<String>("Venda ativada com sucesso.", HttpStatus.OK);
	}

	@GetMapping(value = "/{valor}/{tipoConsulta}")
	public ResponseEntity<List<VendaDTO>> buscarVendasDinamico(@PathVariable String valor, @PathVariable String tipoConsulta) {
		List<VendaModel> vendas = null; 
		
		if (tipoConsulta.equals("CODIGO_PRODUTO")) {
			vendas = vendaRepository.findVendasPorProdutoId(Long.parseLong(valor));
		} else if (tipoConsulta.equals("NOME_PRODUTO")) {	
			vendas = vendaRepository.findVendasPorNomeProduto(valor.trim().toUpperCase());
		} else if (tipoConsulta.equals("NOME_CLIENTE")) {	
			vendas = vendaRepository.findVendasPorNomeCliente(valor.trim().toUpperCase());
		} else if (tipoConsulta.equals("ENDERECO_COBRANCA")) {	
			vendas = vendaRepository.findVendasPorEndereco(valor.trim().toUpperCase(), "COBRANCA");
		} else if (tipoConsulta.equals("ENDERECO_ENTREGA")) {	
			vendas = vendaRepository.findVendasPorEndereco(valor.trim().toUpperCase(), "ENTREGA");
		} else if (tipoConsulta.equals("CPF_CLIENTE")) {	
			vendas = vendaRepository.findVendasPorCpfCliente(valor.trim());
		} else if (tipoConsulta.equals("CODIGO_CLIENTE")) {	
			vendas = vendaRepository.findByPessoaIdAndExcluidoFalse(Long.parseLong(valor));
		}

		List<VendaDTO> vendasDto = new ArrayList<VendaDTO>();
		for(VendaModel venda : vendas) {
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
	
			vendasDto.add(vendaDto);
		}

		return new ResponseEntity<List<VendaDTO>>(vendasDto, HttpStatus.OK);
	}

	@GetMapping(value = "/datas/{data1}/{data2}")
	public ResponseEntity<List<VendaDTO>> buscarVendasData(@PathVariable String data1, @PathVariable String data2) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date dataInicio = dateFormat.parse(data1);
        Date dataFim = dateFormat.parse(data2);
		
		List<VendaModel> vendas = vendaRepository.findByDataVendaBetweenAndExcluidoFalse(dataInicio, dataFim);

		if (vendas == null) {
			vendas = new ArrayList<VendaModel>();
		}
		
		List<VendaDTO> vendasDto = new ArrayList<VendaDTO>();
		for(VendaModel venda : vendas) {
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
	
			vendasDto.add(vendaDto);
		}

		return new ResponseEntity<List<VendaDTO>>(vendasDto, HttpStatus.OK);
	}

}
