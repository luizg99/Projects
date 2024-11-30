package com.lojavirtual.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lojavirtual.model.dto.RelatorioVendaStatusDTO;
import com.lojavirtual.model.dto.MelhorEnvio.ConsultaFreteDTO;
import com.lojavirtual.model.dto.MelhorEnvio.EmpresaTransporteDTO;
import com.lojavirtual.security.ApiTokenIntegracao;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class VendaService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Este relatório permite a empresa saber quais produtos estão com pouco estoque.
	 * Os produtos precisam estar configurados com o alerta de estoque.
	 *
	 * @param condicoesDTO
	 * @return List<RelatorioComprasProdutosNotaFiscalDTO>
	 *
	 * @author Nunes
	 */
	public List<RelatorioVendaStatusDTO> gerarRelatorioVendaStatus(RelatorioVendaStatusDTO condicoesDTO) {
		String sql =
				"select\n" +
				"    pro.id as codigo_produto,\n" +
				"    pro.nome as nome_produto,\n" +
				"    pro.valor_venda as valor_venda_produto,\n" +
				"    pef.id as codigo_cliente,\n" +
				"    pef.nome as nome_cliente,\n" +
				"    pef.email as email,\n" +
				"    pef.telefone as telefone_cliente,\n" +
				"    ven.id as codigo_venda,\n" +
				"    ven.status as status_venda\n" +
				"from\n" +
				"    vendas VEN\n" +
				"\n" +
				"inner join vendas_itens vei\n" +
				"on vei.venda_id = ven.id\n" +
				"\n" +
				"inner join produtos pro\n" +
				"on pro.id = vei.produto_id\n" +
				"\n" +
				"inner join pessoas_fisicas pef\n" +
				"on pef.id = ven.pessoa_id\n" +
				"where 1 = 1\n";

		if (!condicoesDTO.getDataInicial().isEmpty()) {
			sql += "and VEN.DATA_COMPRA >= '" + condicoesDTO.getDataInicial() + "'\n";
		}

		if (!condicoesDTO.getDataFinal().isEmpty()) {
			sql += "and VEN.DATA_COMPRA <= '" + condicoesDTO.getDataInicial() + "'\n";
		}

		if (!condicoesDTO.getCodigoVenda().isEmpty()) {
			sql += "and VEN.ID = " + condicoesDTO.getCodigoVenda() + "\n";
		}

		if (!condicoesDTO.getCodigoProduto().isEmpty()) {
			sql += "and PRO.ID = " + condicoesDTO.getCodigoProduto() + "\n";
		}

		if (!condicoesDTO.getNomeProduto().isEmpty()) {
			sql += "and upper(PRO.nome) like upper('%" + condicoesDTO.getNomeProduto() +   "%')" + "\n";
		}

		if (!condicoesDTO.getNomeCliente().isEmpty()) {
			sql += "and upper(PEF.nome) like upper('%" + condicoesDTO.getNomeCliente() +   "%')" + "\n";
		}

		if (!condicoesDTO.getCodigoCliente().isEmpty()) {
			sql += "and PEF.ID = " + condicoesDTO.getCodigoCliente()  + "\n";
		}

		if (!condicoesDTO.getStatusVenda().isEmpty()) {
			sql += "and VEN.STATUS = '" + condicoesDTO.getStatusVenda()  + "'\n";
		}

		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RelatorioVendaStatusDTO.class));
	}

	public List<EmpresaTransporteDTO> consultaFretes(ConsultaFreteDTO consultaFreteDTO) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(consultaFreteDTO);

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json);
		Request request = new Request.Builder()
				.url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/calculate")
				.post(body)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BX)
				.addHeader("User-Agent", "luizfasam@gmail.com")
				.build();

		Response response = client.newCall(request).execute();
		JsonNode jsonNode = new ObjectMapper().readTree(response.body().string());
		Iterator<JsonNode> iterator = jsonNode.iterator();

		List<EmpresaTransporteDTO> empresas = new ArrayList<>();

		while(iterator.hasNext()) {
			JsonNode node = iterator.next();

			EmpresaTransporteDTO empresaDto = new EmpresaTransporteDTO();

			if (node.get("id") != null) {
				empresaDto.setId(node.get("id").asText());
			}

			if (node.get("name") != null) {
				empresaDto.setNome(node.get("name").asText());
			}

			if (node.get("price") != null) {
				empresaDto.setValor(node.get("price").asText());
			}

			if (node.get("company") != null) {
				empresaDto.setEmpresa(node.get("company").get("name").asText());
				empresaDto.setPicture(node.get("company").get("picture").asText());
			}

			if (empresaDto.dadosOk()) {
				empresas.add(empresaDto);
			}
		}

		return empresas;
	}

}	