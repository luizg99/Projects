package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "MARCAS_PRODUTOS")
@SequenceGenerator(name = "SEQ_MARCAS_PRODUTOS", sequenceName = "SEQ_MARCAS_PRODUTOS", allocationSize = 1, initialValue = 1) 
public class MarcaProdutoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MARCAS_PRODUTOS")
    private Long id;

    @Column(nullable = false)
    private String descricao;
    
    @ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_MARCAS_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;
    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
		MarcaProdutoModel other = (MarcaProdutoModel) obj;
		return Objects.equals(id, other.id);
	}

    

}