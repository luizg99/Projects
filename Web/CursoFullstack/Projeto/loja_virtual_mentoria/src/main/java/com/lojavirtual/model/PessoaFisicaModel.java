package com.lojavirtual.model;

import java.util.Date;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "PESSOAS_FISICAS")
@PrimaryKeyJoinColumn(name = "id")
public class PessoaFisicaModel extends PessoaModel {

	private static final long serialVersionUID = 1L;
	
	@CPF(message = "CPF está inválido")
	@Column(nullable = false)
	private String cpf;
	
	@Temporal(TemporalType.DATE)
	private Date dataNascimento;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	
}
