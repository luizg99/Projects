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
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "FORMAS_PAGAMENTOS")
@SequenceGenerator(name = "SEQ_FORMAS_PAGAMENTOS", sequenceName = "SEQ_FORMAS_PAGAMENTOS", allocationSize = 1, initialValue = 1) 
public class FormaPagamentoModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FORMAS_PAGAMENTOS")
    private Long id;

	@NotNull(message = "Informe a descrição.")
	@Column(nullable = false)
    private String descricao;
	
	@NotNull(message = "Informe a empresa.")
	@ManyToOne(targetEntity = PessoaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_FORM_PAGAM_EMPRESA_ID"))
	private PessoaModel empresa;
	    
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

	public PessoaModel getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(PessoaModel empresa) {
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
		FormaPagamentoModel other = (FormaPagamentoModel) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
