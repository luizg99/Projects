package com.lojavirtual.enums;

public enum StatusVenda {

	FINALIZADA("Finalizada"),
	CANCELADA("Cancelada"),
	ABANDONADA("Abandonada");

	private String descricao;

	private StatusVenda(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return this.descricao;
	}
}