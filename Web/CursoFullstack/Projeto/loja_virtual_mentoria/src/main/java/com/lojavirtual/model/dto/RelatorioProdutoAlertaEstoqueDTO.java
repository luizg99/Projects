package com.lojavirtual.model.dto;

import java.io.Serializable;

public class RelatorioProdutoAlertaEstoqueDTO implements  Serializable {

	private static final long serialVersionUID = 1L;

	private String nomeProduto = "";

	private String codigoProduto = "";

	private String quantidadeEstoque = "";

	private String quantidadeAlertaEstoque = "";

	public String getNomeProduto() {
		return nomeProduto;
	}

	public void setNomeProduto(String nomeProduto) {
		this.nomeProduto = nomeProduto;
	}

	public String getCodigoProduto() {
		return codigoProduto;
	}

	public void setCodigoProduto(String codigoProduto) {
		this.codigoProduto = codigoProduto;
	}

	public String getQuantidadeAlertaEstoque() {
		return quantidadeAlertaEstoque;
	}

	public void setQuantidadeAlertaEstoque(String quantidadeAlertaEstoque) {
		this.quantidadeAlertaEstoque = quantidadeAlertaEstoque;
	}

	public String getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(String quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}
}