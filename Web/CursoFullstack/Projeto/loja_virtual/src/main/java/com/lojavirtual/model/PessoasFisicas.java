package com.lojavirtual.model;

import java.util.Date;

import ch.qos.logback.core.subst.Token.Type;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "PESSOAS_FISICAS")
@PrimaryKeyJoinColumn(name = "id")
public class PessoasFisicas extends Pessoas{

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String Cpf;
	
	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date DataNascimento;
	
	public String getCpf() {
		return Cpf;
	}

	public void setCpf(String cpf) {
		Cpf = cpf;
	}

	public Date getDataNascimento() {
		return DataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		DataNascimento = dataNascimento;
	}
	
}
