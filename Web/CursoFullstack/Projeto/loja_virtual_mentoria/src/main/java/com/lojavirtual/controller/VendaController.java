package com.lojavirtual.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lojavirtual.enums.StatusContaReceber;
import com.lojavirtual.model.ContaReceberModel;
import com.lojavirtual.model.EnderecoModel;
import com.lojavirtual.model.PessoaFisicaModel;
import com.lojavirtual.model.StatusRastreioModel;
import com.lojavirtual.model.VendaItemModel;
import com.lojavirtual.model.VendaModel;
import com.lojavirtual.model.dto.RelatorioVendaStatusDTO;
import com.lojavirtual.model.dto.VendaDTO;
import com.lojavirtual.model.dto.VendaItemDTO;
import com.lojavirtual.model.dto.MelhorEnvio.ConsultaFreteDTO;
import com.lojavirtual.model.dto.MelhorEnvio.EmpresaTransporteDTO;
import com.lojavirtual.model.dto.MelhorEnvio.envioEtiqueta.EnvioEtiquetaDTO;
import com.lojavirtual.model.dto.MelhorEnvio.envioEtiqueta.ProductsEnvioEtiquetaDTO;
import com.lojavirtual.model.dto.MelhorEnvio.envioEtiqueta.TagsEnvioEtiquetaDto;
import com.lojavirtual.model.dto.MelhorEnvio.envioEtiqueta.VolumesEnvioEtiquetaDTO;
import com.lojavirtual.repository.ContaReceberRepository;
import com.lojavirtual.repository.EnderecoRepository;
import com.lojavirtual.repository.NotaFiscalVendaRepository;
import com.lojavirtual.repository.StatusRastreioRepository;
import com.lojavirtual.repository.VendaRepository;
import com.lojavirtual.security.ApiTokenIntegracao;
import com.lojavirtual.service.EnvioEmailService;
import com.lojavirtual.service.VendaService;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.MediaType;

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
    @Autowired
    private VendaService vendaService;

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

	@PostMapping(value = "/relatorioStatusVenda")
	public ResponseEntity<List<RelatorioVendaStatusDTO>> relatorioStatusVenda(@RequestBody RelatorioVendaStatusDTO relatorioVendaStatusDTO) {
		List<RelatorioVendaStatusDTO> relatorio = new ArrayList<RelatorioVendaStatusDTO>();

		relatorio = vendaService.gerarRelatorioVendaStatus(relatorioVendaStatusDTO);

		return ResponseEntity.ok(relatorio);
	}

	@PostMapping(value = "/consultaFrete")
	public ResponseEntity<List<EmpresaTransporteDTO>> consultaFrete(@RequestBody @Valid ConsultaFreteDTO consultaFreteDTO) throws IOException {
		return ResponseEntity.ok(this.vendaService.consultaFretes(consultaFreteDTO));
	}
	

	@GetMapping(value = "/imprimeEtiquetaFrete/{id}")
	public ResponseEntity<String> imprimeEtiquetaFrete(@PathVariable Long id) throws ExceptionMentoriaJava, IOException {
		VendaModel venda = vendaRepository.findById(id).orElse(null);
		if (venda == null) {
			return ResponseEntity.ok("Venda não encontrada");
		}

		EnvioEtiquetaDTO envioEtiquetaDTO = new EnvioEtiquetaDTO();
		envioEtiquetaDTO.setService(venda.getServicoTransportadora());
		envioEtiquetaDTO.setAgency("49");

		envioEtiquetaDTO.getFrom().setName(venda.getEmpresa().getNome());
		envioEtiquetaDTO.getFrom().setPhone(venda.getEmpresa().getTelefone());
		envioEtiquetaDTO.getFrom().setEmail(venda.getEmpresa().getEmail());
		envioEtiquetaDTO.getFrom().setCompany_document(venda.getEmpresa().getCnpj());
		envioEtiquetaDTO.getFrom().setState_register(venda.getEmpresa().getInscricaoEstadual());
		envioEtiquetaDTO.getFrom().setAddress(venda.getEmpresa().getEnderecos().get(0).getLogradouro());
		envioEtiquetaDTO.getFrom().setComplement(venda.getEmpresa().getEnderecos().get(0).getComplemento());
		envioEtiquetaDTO.getFrom().setNumber(venda.getEmpresa().getEnderecos().get(0).getNumero());
		envioEtiquetaDTO.getFrom().setDistrict(venda.getEmpresa().getEnderecos().get(0).getBairro());
		envioEtiquetaDTO.getFrom().setCity(venda.getEmpresa().getEnderecos().get(0).getCidade());
		envioEtiquetaDTO.getFrom().setCountry_id("BR");
		envioEtiquetaDTO.getFrom().setPostal_code(venda.getEmpresa().getEnderecos().get(0).getCep());
		envioEtiquetaDTO.getFrom().setNote("");

		envioEtiquetaDTO.getTo().setName(venda.getPessoa().getNome());
		envioEtiquetaDTO.getTo().setPhone(venda.getPessoa().getTelefone());
		envioEtiquetaDTO.getTo().setEmail(venda.getPessoa().getEmail());
		envioEtiquetaDTO.getTo().setDocument(venda.getPessoa().getCpf());
		envioEtiquetaDTO.getTo().setAddress(venda.getPessoa().getEnderecos().get(0).getLogradouro());
		envioEtiquetaDTO.getTo().setComplement(venda.getPessoa().getEnderecos().get(0).getComplemento());
		envioEtiquetaDTO.getTo().setNumber(venda.getPessoa().getEnderecos().get(0).getNumero());
		envioEtiquetaDTO.getTo().setDistrict(venda.getPessoa().getEnderecos().get(0).getBairro());
		envioEtiquetaDTO.getTo().setCity(venda.getPessoa().getEnderecos().get(0).getCidade());
		envioEtiquetaDTO.getTo().setState_abbr(venda.getPessoa().getEnderecos().get(0).getUf());
		envioEtiquetaDTO.getTo().setCountry_id("BR");
		envioEtiquetaDTO.getTo().setPostal_code(venda.getPessoa().getEnderecos().get(0).getCep());
		envioEtiquetaDTO.getTo().setNote("");

		List<ProductsEnvioEtiquetaDTO> productsEnvioEtiquetaDTOS = new ArrayList<>();
		for(VendaItemModel item : venda.getItens()) {
			ProductsEnvioEtiquetaDTO product = new ProductsEnvioEtiquetaDTO();
			product.setName(item.getProduto().getNome());
			product.setQuantity(item.getQuantidade().toString());
			product.setUnitary_value(item.getProduto().getValorVenda().toString());

			productsEnvioEtiquetaDTOS.add(product);
		}
		envioEtiquetaDTO.setProducts(productsEnvioEtiquetaDTOS);

		List<VolumesEnvioEtiquetaDTO> volumes = new ArrayList<>();
		for(VendaItemModel item : venda.getItens()) {
			VolumesEnvioEtiquetaDTO volume = new VolumesEnvioEtiquetaDTO();

			volume.setHeight(item.getProduto().getAltura().toString());
			volume.setLength(item.getProduto().getProfundidade().toString());
			volume.setWidth(item.getProduto().getLargura().toString());
			volume.setWeight(item.getProduto().getPeso().toString());

			volumes.add(volume);
		}
		envioEtiquetaDTO.setVolumes(volumes);

		envioEtiquetaDTO.getOptions().setInsurance_value(venda.getValorTotal().toString());
		envioEtiquetaDTO.getOptions().setReceipt(false);
		envioEtiquetaDTO.getOptions().setReverse(false);
		envioEtiquetaDTO.getOptions().setOwn_hand(false);
		envioEtiquetaDTO.getOptions().setNon_commercial(false);
		envioEtiquetaDTO.getOptions().getInvoice().setKey("31190307586261000184550010000092481404848162");
		envioEtiquetaDTO.getOptions().setPlatform(venda.getEmpresa().getNomeFantasia());

		TagsEnvioEtiquetaDto tag = new TagsEnvioEtiquetaDto();
		tag.setTag("Identificação do pedido: " + venda.getId());
		tag.setUrl(null);
		envioEtiquetaDTO.getOptions().getTags().add(tag);
		
		
		//Inserindo frete - Requisitando etiqueta
		String json = new ObjectMapper().writeValueAsString(envioEtiquetaDTO);
		
		
		OkHttpClient client = new OkHttpClient();
		
		okhttp3.MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json);
		okhttp3.Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/cart")
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BX)
                .addHeader("User-Agent", "luizfasam@gmail.com")
                .build();

		okhttp3.Response response = client.newCall(request).execute();

		String respostaJson = response.body().string();

		if (respostaJson.contains("error")) {
			throw new ExceptionMentoriaJava(respostaJson);
		}

		JsonNode jsonNode = new ObjectMapper().readTree(respostaJson);
		Iterator<JsonNode> iterator = jsonNode.iterator();

		String idEtiqueta = "";
		while (iterator.hasNext()) {
			JsonNode node = iterator.next();
			if (node.get("id").asText() != null) {
				idEtiqueta = node.get("id").asText();
			} else {
				idEtiqueta = node.asText();
			}
			break;
		}
		
		//Salvando o código da etiqueta.
		vendaRepository.updateCodigoEtiqueta(idEtiqueta, venda.getId());
		
		//Fazendo a compra da etiqueta.
		OkHttpClient clientCompraEtiqueta = new OkHttpClient();

		okhttp3.MediaType mediaTypeCompraEtiqueta = MediaType.parse("application/json");
		okhttp3.RequestBody bodyCompraEtiqueta = okhttp3.RequestBody.create(mediaTypeCompraEtiqueta, "{\n \"orders\": [\n \"" + idEtiqueta + "\"\n ]\n }" );
		okhttp3.Request requestCompraEtiqueta = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/checkout")
                .post(bodyCompraEtiqueta)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BX)
                .addHeader("User-Agent", "luizfasam@gmail.com")
                .build();

		okhttp3.Response responseCompraEtiqueta = clientCompraEtiqueta.newCall(requestCompraEtiqueta).execute();

		if (!responseCompraEtiqueta.isSuccessful()) {
			return ResponseEntity.ok("Não foi possível realizar a compra da etiqueta.");
		}
		
		//Fazendo a impressão da etiqueta.
		OkHttpClient clientGeracao = new OkHttpClient();

		okhttp3.MediaType mediaTypeGeracao = MediaType.parse("application/json");
		okhttp3.RequestBody bodyGeracao = okhttp3.RequestBody.create(mediaTypeGeracao, "{\n \"orders\": [\n \"" + idEtiqueta + "\"\n ]\n }");
		okhttp3.Request requestGeracao = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/generate")
                .post(bodyGeracao)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BX)
                .addHeader("User-Agent", "luizfasam@gmail.com")
                .build();

		okhttp3.Response responseGeracao = clientGeracao.newCall(requestGeracao).execute();

		if (!responseGeracao.isSuccessful()) {
			return ResponseEntity.ok("Não foi possível gerar a etiqueta.");
		}
		
		//Imprimindo a etiqueta
		OkHttpClient clientImprime = new OkHttpClient();

		okhttp3.MediaType mediaTypeImprime = MediaType.parse("application/json");
		okhttp3.RequestBody bodyImprime = okhttp3.RequestBody.create(mediaTypeImprime, "{\n \"orders\": [\n \"" + idEtiqueta + "\"\n ]\n }");
		okhttp3.Request requestImprime = new Request.Builder()
				.url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/print")
				.post(bodyImprime)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BX)
				.addHeader("User-Agent", "luizfasam@gmail.com")
				.build();

		okhttp3.Response responseImprime = clientImprime.newCall(requestImprime).execute();

		if (!responseImprime.isSuccessful()) {
			return ResponseEntity.ok("Não foi possível imprimir a etiqueta.");
		}

		String urlEtiqueta = responseImprime.body().string();
		vendaRepository.updateUrlEtiqueta(urlEtiqueta.replaceAll("'\'", ""), venda.getId());

		return ResponseEntity.ok("Sucesso");
	}
/*
	@GetMapping(value = "/cancelarEtiqueta/{vendaId}/{descricao}")
	public ResponseEntity<String> cancelarEtiqueta(@PathVariable Long vendaId, @PathVariable String reason_id, @PathVariable String descricao) throws IOException {
		VendaModel venda = vendaRepository.findById(vendaId).orElse(null);
		if (venda == null) {
			return ResponseEntity.notFound().build();
		}

		OkHttpClient client = new OkHttpClient();

		okhttp3.MediaType mediaType = MediaType.parse("application/json");
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\"order\":{\"reason_id\":\"" + reason_id +"\",\"id\":\"" + venda.getCodigoEtiqueta() +"\",\"description\":\"" + descricao +"\"}}");
		okhttp3.Request request = new Request.Builder()
				.url(ApiTokenIntegracao.URL_MELHOR_ENVIO + "api/v2/me/shipment/cancel")
				.post(body)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO)
				.addHeader("User-Agent", "lucasnunnes40@gmail.com")
				.build();

		okhttp3.Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			return ResponseEntity.ok("Falha ao cancelar etiqueta.");
		}

		return ResponseEntity.ok(response.body().string());
	}
*/
	
}