package com.lojavirtual.enums;

public enum StatusContasPagar {
	
	COBRANCA("PAGAR"),
	VENCIDA("VENCIDA"),
	ABERTA("ABERTA"),
	QUITADA("QUITADA"),
	NEGOCIADA("NEGOCIADA");
	
	private String descricao;
	
	private StatusContasPagar(String descricao) {
		this.descricao = descricao;
		// TODO Auto-generated constructor stub
	}
	
	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.descricao;
	}
}
