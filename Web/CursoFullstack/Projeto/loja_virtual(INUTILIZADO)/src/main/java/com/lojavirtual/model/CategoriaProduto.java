package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "CATEGORIAS_PRODUTOS")
@SequenceGenerator(name = "SEQ_CATEGORIAS_PRODUTOS", sequenceName = "SEQ_CATEGORIAS_PRODUTOS", allocationSize = 1, initialValue = 1) 
public class CategoriaProduto implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CATEGORIAS_PRODUTOS")
    private Long id;
    
    @Column(name = "DESCRICAO", nullable = false)
    private String Descricao;
    
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "empresa_id", nullable = false, 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CATEGORIAS_PRODUTOS_EMP_ID"))
    private PessoaJuridicaModel empresa;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return Descricao;
	}

	public void setDescricao(String descricao) {
		this.Descricao = descricao;
	}
	
	public PessoaJuridicaModel getEmpresa() {
		return empresa;
	}

	public void setEmpresa(PessoaJuridicaModel empresa) {
		this.empresa = empresa;
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
		CategoriaProduto other = (CategoriaProduto) obj;
		return Objects.equals(id, other.id);
	}

	


}
