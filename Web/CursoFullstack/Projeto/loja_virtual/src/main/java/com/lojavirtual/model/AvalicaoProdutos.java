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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "AVALIACAO_PRODUTOS")
@SequenceGenerator(name = "SEQ_AVALIACAO_PRODUTOS", sequenceName = "SEQ_AVALIACAO_PRODUTOS", allocationSize = 1, initialValue = 1) 
public class AvalicaoProdutos implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AVALIACAO_PRODUTOS")
    private Long id;
    
    @Column(nullable = false)
    private String Descricao;

    @Column(nullable = false)
    private Integer Nota;
    
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_AVALIACAO_PRODUTO_PESSOA_ID"))
    private Pessoas pessoa_id;

    @OneToOne(targetEntity = Produtos.class)
    @JoinColumn(name = "PRODUTO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_AVALIACAO_PRODUTO_PRODUTO_ID"))
    private Produtos Produto;
    
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "empresa_id", nullable = false, 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_AVALIACAO_PRODUTOS_EMP_ID"))
    private Pessoas Empresa;    

	public Pessoas getEmpresa() {
		return Empresa;
	}

	public void setEmpresa(Pessoas empresa) {
		Empresa = empresa;
	}

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

	public Integer getNOTA() {
		return Nota;
	}

	public void setNOTA(Integer nOTA) {
		Nota = nOTA;
	}

	public Pessoas getPessoa_id() {
		return pessoa_id;
	}

	public void setPessoa_id(Pessoas pessoa_id) {
		this.pessoa_id = pessoa_id;
	}

	public Produtos getProduto() {
		return Produto;
	}

	public void setProduto(Produtos produto) {
		Produto = produto;
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
		AvalicaoProdutos other = (AvalicaoProdutos) obj;
		return Objects.equals(id, other.id);
	}

	


}
