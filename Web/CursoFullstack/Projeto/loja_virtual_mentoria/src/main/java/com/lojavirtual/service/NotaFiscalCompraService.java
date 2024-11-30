package com.lojavirtual.service;

import com.lojavirtual.model.dto.RelatorioComprasProdutosNotaFiscalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotaFiscalCompraService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<RelatorioComprasProdutosNotaFiscalDTO> gerarRelatorioComprasProdutos(RelatorioComprasProdutosNotaFiscalDTO condicoesDTO) {
		List<RelatorioComprasProdutosNotaFiscalDTO> relatorio = new ArrayList<RelatorioComprasProdutosNotaFiscalDTO>();

		String sql =
				"select\n" +
				"  PRO.id as CODIGO_PRODUTO,\n" +
				"  PRO.NOME as NOME_PRODUTO,\n" +
				"  PRO.valor_venda as VALOR_VENDA_PRODUTO,\n" +
				"  NFI.quantidade as QUANTIDADE_COMPRADA,\n" +
				"  PJU.ID as CODIGO_FORNECEDOR,\n" +
				"  PJU.nome as NOME_FORNECEDOR,\n" +
				"  NFC.data_compra as DATA_COMPRA,\n" +
				"  NFC.ID as CODIGO_NOTA\n" +
				"from\n" +
				"    notas_fiscais_compras NFC\n" +
				"\n" +
				"inner join notas_fiscais_compras_itens NFI\n" +
				"on NFC.id = NFI.nota_fiscal_compra_id\n" +
				"\n" +
				"inner join produtos PRO\n" +
				"on NFI.produto_id = PRO.id\n" +
				"\n" +
				"inner join pessoas_juridicas PJU\n" +
				"on NFC.pessoa_id = PJU.id\n" +
				"where 1 = 1\n";

		if (!condicoesDTO.getDataInicial().isEmpty()) {
			sql += "and NFC.DATA_COMPRA >= '" + condicoesDTO.getDataInicial() + "'\n";
		}

		if (!condicoesDTO.getDataFinal().isEmpty()) {
			sql += "and NFC.DATA_COMPRA <= '" + condicoesDTO.getDataInicial() + "'\n";
		}

		if (!condicoesDTO.getCodigoNota().isEmpty()) {
			sql += "and NFC.ID = " + condicoesDTO.getCodigoNota() + "\n";
		}

		if (!condicoesDTO.getCodigoProduto().isEmpty()) {
			sql += "and PRO.ID = " + condicoesDTO.getCodigoProduto() + "\n";
		}

		if (!condicoesDTO.getNomeProduto().isEmpty()) {
			sql += "and upper(PRO.nome) like upper('%" + condicoesDTO.getNomeProduto() +   "%')" + "\n";
		}

		if (!condicoesDTO.getNomeFornecedor().isEmpty()) {
			sql += "and upper(PJU.nome) like upper('%" + condicoesDTO.getNomeFornecedor() +   "%')" + "\n";
		}

		relatorio = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RelatorioComprasProdutosNotaFiscalDTO.class));

		return relatorio;
	}
}
