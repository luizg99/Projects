package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Objects;

import com.lojavirtual.enums.TipoEndereco;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@jakarta.persistence.Entity
@Table(name = "ENDERECOS")
@SequenceGenerator(name = "SEQ_ENDERECOS", sequenceName = "SEQ_ENDERECOS", allocationSize = 1, initialValue = 1) 
public class Enderecos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ENDERECOS")
    private Long id;
    
    @Column(nullable = false)
    private String Rua;
    
    @Column(nullable = false)
    private String Cep;
    
    @Column(nullable = false)
    private String Numero;
    
    private String Complemento;
    
    @Column(nullable = false)
    private String Bairro;
    
    @Column(nullable = false)
    private String Cidade;
    
    @Column(nullable = false)
    private String Uf;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEndereco TipoEndereco;
    
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_ENDERECOS_PESSOA_ID"))
    private Pessoas Pessoa;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRua() {
		return Rua;
	}

	public void setRua(String rua) {
		Rua = rua;
	}

	public String getCep() {
		return Cep;
	}

	public void setCep(String cep) {
		Cep = cep;
	}

	public String getNumero() {
		return Numero;
	}

	public void setNumero(String numero) {
		Numero = numero;
	}

	public String getComplemento() {
		return Complemento;
	}

	public void setComplemento(String complemento) {
		Complemento = complemento;
	}

	public String getBairro() {
		return Bairro;
	}

	public void setBairro(String bairro) {
		Bairro = bairro;
	}

	public String getCidade() {
		return Cidade;
	}

	public void setCidade(String cidade) {
		Cidade = cidade;
	}

	public String getUf() {
		return Uf;
	}

	public void setUf(String uf) {
		Uf = uf;
	}

	public TipoEndereco getTipoEndereco() {
		return TipoEndereco;
	}

	public void setTipoEndereco(TipoEndereco tipoEndereco) {
		TipoEndereco = tipoEndereco;
	}

	public Pessoas getPessoa() {
		return Pessoa;
	}

	public void setPessoa(Pessoas pessoa) {
		Pessoa = pessoa;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Enderecos other = (Enderecos) obj;
		return Objects.equals(id, other.id);
	}
    
}