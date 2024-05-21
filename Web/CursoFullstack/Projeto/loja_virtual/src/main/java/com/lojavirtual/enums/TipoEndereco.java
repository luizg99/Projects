package com.lojavirtual.enums;

public enum TipoEndereco {
	
	COBRANCA("Cobrança"),
	ENTREGA("Entrega");
	
	private String descricao;
	
	private TipoEndereco(String descricao) {
		this.descricao = descricao;
		// TODO Auto-generated constructor stub
	}
	
	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
