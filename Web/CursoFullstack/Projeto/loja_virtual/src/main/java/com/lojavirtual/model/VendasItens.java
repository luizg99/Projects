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
@Table(name = "VENDAS_ITENS")
@SequenceGenerator(name = "SEQ_VENDAS_ITENS", sequenceName = "SEQ_VENDAS_ITENS", allocationSize = 1, initialValue = 1) 
public class VendasItens implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_VENDAS_ITENS")
    private Long id;
    
    @OneToOne(targetEntity = Produtos.class)
    @JoinColumn(name = "PRODUTO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_ITENS_PRODUTO_ID"))
    private Produtos Produto;
    
    @OneToOne(targetEntity = Vendas.class)
    @JoinColumn(name = "VENDA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_ITENS_VENDA_ID"))
    private Vendas Venda;
    
    @Column(nullable = false)
    private Double Quantidade;
    
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "empresa_id", nullable = false, 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_EMPRESA_ID"))
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

	public Produtos getProduto() {
		return Produto;
	}

	public void setProduto(Produtos produto) {
		Produto = produto;
	}

	public Vendas getVenda() {
		return Venda;
	}

	public void setVenda(Vendas venda) {
		Venda = venda;
	}

	public Double getQuantidade() {
		return Quantidade;
	}

	public void setQuantidade(Double quantidade) {
		Quantidade = quantidade;
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
		VendasItens other = (VendasItens) obj;
		return Objects.equals(id, other.id);
	}

	


}
