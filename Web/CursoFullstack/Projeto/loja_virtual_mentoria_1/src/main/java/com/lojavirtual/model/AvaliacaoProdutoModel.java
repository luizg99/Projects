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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "AVALIACOES_PRODUTOS")
@SequenceGenerator(name = "SEQ_AVALIACOES_PRODUTOS", sequenceName = "SEQ_AVALIACOES_PRODUTOS", allocationSize = 1, initialValue = 1) 
public class AvaliacaoProdutoModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AVALIACOES_PRODUTOS")
    private Long id;

	@NotEmpty(message = "Informe uma descrição para a avaliação do produto")
	@Column(nullable = false)
    private String descricao;
	
	@Min(value = 1, message = "Não é possível dar uma nota menor que 0.")
	@Max(value = 10, message = "Não é possível dar uma nota maior que 10.")
	@Column(nullable = false)
	private Integer nota;
	
	@ManyToOne
	@JoinColumn(name = "PRODUTO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_AVALI_PROD_PRODUTO_ID"))
	private ProdutoModel produto;
	
	@ManyToOne(targetEntity = PessoaFisicaModel.class)
	@JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_AVALI_PROD_PESSOA_ID"))
	private PessoaFisicaModel pessoa;
	
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_AVALI_PROD_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;
	
	public PessoaFisicaModel getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaFisicaModel pessoa) {
		this.pessoa = pessoa;
	}

	public PessoaJuridicaModel getEmpresa() {
		return empresa;
	}

	public void setEmpresa(PessoaJuridicaModel empresa) {
		this.empresa = empresa;
	}

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
    
	public Integer getNota() {
		return nota;
	}

	public void setNota(Integer nota) {
		this.nota = nota;
	}

	public ProdutoModel getProduto() {
		return produto;
	}

	public void setProduto(ProdutoModel produto) {
		this.produto = produto;
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
		AvaliacaoProdutoModel other = (AvaliacaoProdutoModel) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
