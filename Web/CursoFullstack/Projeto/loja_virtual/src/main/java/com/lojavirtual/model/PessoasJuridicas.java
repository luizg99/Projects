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
	private String cpf;
	
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getINSC_ESTADUAL() {
		return INSC_ESTADUAL;
	}
	public void setINSC_ESTADUAL(String iNSC_ESTADUAL) {
		INSC_ESTADUAL = iNSC_ESTADUAL;
	}
	public String getINSC_MUNICIPAL() {
		return INSC_MUNICIPAL;
	}
	public void setINSC_MUNICIPAL(String iNSC_MUNICIPAL) {
		INSC_MUNICIPAL = iNSC_MUNICIPAL;
	}
	public String getRAZAO_SOCIAL() {
		return RAZAO_SOCIAL;
	}
	public void setRAZAO_SOCIAL(String rAZAO_SOCIAL) {
		RAZAO_SOCIAL = rAZAO_SOCIAL;
	}
	public String getFANTASIA() {
		return FANTASIA;
	}
	public void setFANTASIA(String fANTASIA) {
		FANTASIA = fANTASIA;
	}
	public String getCATEGORIA() {
		return CATEGORIA;
	}
	public void setCATEGORIA(String cATEGORIA) {
		CATEGORIA = cATEGORIA;
	}
	private String INSC_ESTADUAL;
	private String INSC_MUNICIPAL;
	private String RAZAO_SOCIAL;
	private String FANTASIA;
	private String CATEGORIA;
	
	
	
}
