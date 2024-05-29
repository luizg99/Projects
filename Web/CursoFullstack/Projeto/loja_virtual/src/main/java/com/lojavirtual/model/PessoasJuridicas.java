package com.lojavirtual.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "PESSOAS_JURIDICAS")
@PrimaryKeyJoinColumn(name = "id")
public class PessoasJuridicas extends Pessoas{

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String Cnpj;
	
	@Column(nullable = false)
	private String InscricaoEstadual;
	
	private String InscricaoMunicipal;
	
	@Column(nullable = false)
	private String RazaoSocial;

	@Column(nullable = false)
	private String Fantasia;
	
	@Column(nullable = false)
	private String Categoria;


	public String getCnpj() {
		return Cnpj;
	}

	public void setCnpj(String cnpj) {
		Cnpj = cnpj;
	}

	public String getInscricaoEstadual() {
		return InscricaoEstadual;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		InscricaoEstadual = inscricaoEstadual;
	}

	public String getInscricaoMunicipal() {
		return InscricaoMunicipal;
	}

	public void setInscricaoMunicipal(String inscricaoMunicipal) {
		InscricaoMunicipal = inscricaoMunicipal;
	}

	public String getRazaoSocial() {
		return RazaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		RazaoSocial = razaoSocial;
	}

	public String getFantasia() {
		return Fantasia;
	}

	public void setFantasia(String fantasia) {
		Fantasia = fantasia;
	}

	public String getCategoria() {
		return Categoria;
	}

	public void setCategoria(String categoria) {
		Categoria = categoria;
	}	
	
}
