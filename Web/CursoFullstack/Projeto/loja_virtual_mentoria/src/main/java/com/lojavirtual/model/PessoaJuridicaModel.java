package com.lojavirtual.model;

import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "PESSOAS_JURIDICAS")
@PrimaryKeyJoinColumn(name = "id")
public class PessoaJuridicaModel extends PessoaModel {

	private static final long serialVersionUID = 1L;
	
	@CNPJ(message = "CNPJ está inválido")
	@Column(nullable = false)
	private String cnpj;
	
	@Column(nullable = false)
	private String inscricaoEstadual;
	
	private String inscricaoMunicipal;
	
	@Column(nullable = false)
	private String razaoSocial;
	
	@Column(nullable = false)
	private String nomeFantasia;
	
	private String categoria;

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	public String getInscricaoMunicipal() {
		return inscricaoMunicipal;
	}

	public void setInscricaoMunicipal(String inscricaoMunicipal) {
		this.inscricaoMunicipal = inscricaoMunicipal;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	
}
