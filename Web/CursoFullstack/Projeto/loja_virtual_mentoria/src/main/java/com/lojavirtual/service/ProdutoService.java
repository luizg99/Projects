package com.lojavirtual.service;

import com.lojavirtual.model.dto.RelatorioComprasProdutosNotaFiscalDTO;
import com.lojavirtual.model.dto.RelatorioProdutoAlertaEstoqueDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Este relatório permite a empresa saber quais produtos estão com pouco estoque.
	 * Os produtos precisam estar configurados com o alerta de estoque.
	 * @return List<RelatorioComprasProdutosNotaFiscalDTO>
	 */
	public List<RelatorioProdutoAlertaEstoqueDTO> gerarRelatorioAlertaEstoque() {
		List<RelatorioProdutoAlertaEstoqueDTO> relatorio = new ArrayList<RelatorioProdutoAlertaEstoqueDTO>();

		String sql =
				"select\n" +
				"  PRO.id as CODIGO_PRODUTO,\n" +
				"  PRO.NOME as NOME_PRODUTO,\n" +
				"  PRO.quantidade_estoque as QUANTIDADE_ESTOQUE,\n" +
				"  PRO.quantidade_alerta_estoque as QUANTIDADE_ALERTA_ESTOQUE\n" +
				"from\n" +
				"    produtos pro\n" +
				"where PRO.quantidade_estoque <= PRO.quantidade_alerta_estoque\n" +
				"and PRO.alerta_quantidade_estoque = True\n";

		relatorio = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RelatorioProdutoAlertaEstoqueDTO.class));

		return relatorio;
	}
}