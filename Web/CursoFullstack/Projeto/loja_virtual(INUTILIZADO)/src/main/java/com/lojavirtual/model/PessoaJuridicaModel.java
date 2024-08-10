package com.lojavirtual.model;

import org.hibernate.validator.constraints.br.CNPJ;

//import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "PESSOAS_JURIDICAS")
@PrimaryKeyJoinColumn(name = "id")
public class PessoaJuridicaModel extends Pessoas {

	private static final long serialVersionUID = 1L;
	
	@CNPJ(message = "CNPJ está inválido")
	@Column(nullable = false)
	private String cnpj;
	
	@Column(nullable = false)
	private String inscricao_estadual;
	
	private String inscricao_municipal;
	
	@Column(nullable = false)
	private String razao_social;
	
	@Column(nullable = false)
	private String fantasia;
	
	private String categoria;

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getInscricao_estadual() {
		return inscricao_estadual;
	}

	public void setInscricao_estadual(String inscricao_estadual) {
		this.inscricao_estadual = inscricao_estadual;
	}

	public String getInscricao_municipal() {
		return inscricao_municipal;
	}

	public void setInscricao_municipal(String inscricao_municipal) {
		this.inscricao_municipal = inscricao_municipal;
	}

	public String getRazao_social() {
		return razao_social;
	}

	public void setRazao_social(String razao_social) {
		this.razao_social = razao_social;
	}

	public String getFantasia() {
		return fantasia;
	}

	public void setFantasia(String fantasia) {
		this.fantasia = fantasia;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}


	
}
