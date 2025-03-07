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
@SequenceGenerator(name = "SEQ_NOTAS_FISCAIS_COMPRAS_ITENS", sequenceName = "SEQ_NOTAS_FISCAIS_COMPRAS_ITENS", allocationSize = 1, initialValue = 1) 
public class NotasFiscaisComprasItens implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FK_NOT_FIS_COMPRAS_ITE_EMP_ID")
    private Long id;
    
    @Column(nullable = false)
    private double Quantidade;
    
    @ManyToOne(targetEntity = NotasFiscaisCompras.class)
    @JoinColumn(name = "NOTA_FISCAL_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NOTAS_FIS_COM_NOTA_ID"))
    private NotasFiscaisCompras NotaFiscalCompra; 
    
    @ManyToOne(targetEntity = Produtos.class)
    @JoinColumn(name = "PRODUTO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NOTAS_FIS_COM_PRODUTO_ID"))
    private Produtos Produto;    
 
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



	public double getQuantidade() {
		return Quantidade;
	}



	public void setQuantidade(double quantidade) {
		Quantidade = quantidade;
	}



	public NotasFiscaisCompras getNotaFiscalCompra() {
		return NotaFiscalCompra;
	}



	public void setNotaFiscalCompra(NotasFiscaisCompras notaFiscalCompra) {
		NotaFiscalCompra = notaFiscalCompra;
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
		NotasFiscaisComprasItens other = (NotasFiscaisComprasItens) obj;
		return Objects.equals(id, other.id);
	}





}
