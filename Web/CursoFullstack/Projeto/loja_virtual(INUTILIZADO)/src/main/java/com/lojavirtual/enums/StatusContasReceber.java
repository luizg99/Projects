package com.lojavirtual.enums;

public enum StatusContasReceber {
	
	COBRANCA("PAGAR"),
	VENCIDA("VENCIDA"),
	ABERTA("ABERTA"),
	QUITADA("QUITADA");	
	
	private String descricao;
	
	private StatusContasReceber(String descricao) {
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
