package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Date;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "NOTAS_FISCAIS_COMPRAS")
@SequenceGenerator(name = "SEQ_NOTAS_FISCAIS_COMPRAS", sequenceName = "SEQ_NOTAS_FISCAIS_COMPRAS", allocationSize = 1, initialValue = 1) 
public class NotasFiscaisCompras implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTAS_FISCAIS_COMPRAS")
    private Long id;
    
    @Column(nullable = false)
    private String Numero;
    
    @Column(nullable = false)
    private String Serie;
    
    private String Observacao;
    
    @Column(nullable = false)
    private Double ValorTotal;
    
    private Double ValorDesconto;
    
    @Column(nullable = false)
    private Double ValorIcms;
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date DataCompra;
    
  
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_PESSOA_ID_NOTA_FISC"))
    private Pessoas Pessoa;
      
    @ManyToOne(targetEntity = ContasPagar.class)
    @JoinColumn(name = "CONTA_PAGAR_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NOTAS_DISCAIS_CONTA_PAG_ID"))
    private ContasPagar ContasPagar;    
      
	public Long getId() {
		return id;
	}




	public void setId(Long id) {
		this.id = id;
	}




	public String getNumero() {
		return Numero;
	}




	public void setNumero(String numero) {
		Numero = numero;
	}




	public String getSerie() {
		return Serie;
	}




	public void setSerie(String serie) {
		Serie = serie;
	}




	public String getObservacao() {
		return Observacao;
	}




	public void setObservacao(String observacao) {
		Observacao = observacao;
	}




	public Double getValorTotal() {
		return ValorTotal;
	}




	public void setValorTotal(Double valorTotal) {
		ValorTotal = valorTotal;
	}




	public Double getValorDesconto() {
		return ValorDesconto;
	}




	public void setValorDesconto(Double valorDesconto) {
		ValorDesconto = valorDesconto;
	}




	public Double getValorIcms() {
		return ValorIcms;
	}




	public void setValorIcms(Double valorIcms) {
		ValorIcms = valorIcms;
	}


	public Date getDataCompra() {
		return DataCompra;
	}




	public void setDataCompra(Date dataCompra) {
		DataCompra = dataCompra;
	}



	public Pessoas getPessoa() {
		return Pessoa;
	}




	public void setPessoa(Pessoas pessoa) {
		Pessoa = pessoa;
	}



	public ContasPagar getContasPagar() {
		return ContasPagar;
	}




	public void setContasPagar(ContasPagar contasPagar) {
		ContasPagar = contasPagar;
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
		NotasFiscaisCompras other = (NotasFiscaisCompras) obj;
		return Objects.equals(id, other.id);
	}



}
