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
@Table(name = "NOTAS_FISCAIS_COMPRAS_ITENS")
@SequenceGenerator(name = "SEQ_NOTAS_COMPRAS_ITENS", sequenceName = "SEQ_NOTAS_COMPRAS_ITENS", allocationSize = 1, initialValue = 1) 
public class NotaItemCompraModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTAS_COMPRAS_ITENS")
    private Long id;

	@Column(nullable = false)
	private Double quantidade;
	
	@ManyToOne(targetEntity = ProdutoModel.class)
	@JoinColumn(name = "PRODUTO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NOTA_ITEM_PRODUTO_ID"))
	private ProdutoModel produto;
	
	@ManyToOne(targetEntity = NotaFiscalCompraModel.class)
	@JoinColumn(name = "NOTA_FISCAL_COMPRA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NOTA_ITEM_NOTA_COMPRA_ID"))
	private NotaFiscalCompraModel notaFiscalCompra;
	
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NOTA_ITEM_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;
	    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public Double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public ProdutoModel getProduto() {
		return produto;
	}

	public void setProduto(ProdutoModel produto) {
		this.produto = produto;
	}

	public NotaFiscalCompraModel getNotaFiscalCompra() {
		return notaFiscalCompra;
	}

	public void setNotaFiscalCompra(NotaFiscalCompraModel notaFiscalCompra) {
		this.notaFiscalCompra = notaFiscalCompra;
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
		NotaItemCompraModel other = (NotaItemCompraModel) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
