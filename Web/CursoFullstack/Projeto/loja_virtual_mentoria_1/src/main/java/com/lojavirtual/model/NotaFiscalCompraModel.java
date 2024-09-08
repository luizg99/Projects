package com.lojavirtual.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "NOTAS_FISCAIS_COMPRAS")
@SequenceGenerator(name = "SEQ_NOTAS_FISCAIS_COMPRAS", sequenceName = "SEQ_NOTAS_FISCAIS_COMPRAS", allocationSize = 1, initialValue = 1) 
public class NotaFiscalCompraModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTAS_FISCAIS_COMPRAS")
    private Long id;

	@NotNull(message = "Informe o número da nota.")
	@Column(nullable = false)
    private String numeroNota;
	
	@NotNull(message = "Informe a série da nota.")
	@Column(nullable = false)
	private String serie;
	
	private String descricaoObservacao;
	
	@NotNull(message = "Informe o valor total da nota.")
	@Column(nullable = false)
	private BigDecimal valorTotal;
    
	private BigDecimal valorDesconto;
	
	@NotNull(message = "Informe o valor do ICMS da nota.")
	@Column(nullable = false)
	private BigDecimal valorIcms;
	
	@NotNull(message = "Informe a data de compra da nota.")
	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataCompra; 
	
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NF_COMPRA_PESSOA_ID"))
	private PessoaJuridicaModel pessoa;
	
	@ManyToOne(targetEntity = ContaPagarModel.class)
	@JoinColumn(name = "PAGAR_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NF_COMPRA_PAGAR_ID"))
	private ContaPagarModel contaPagar;
	
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NF_COMPRA_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public String getNumeroNota() {
		return numeroNota;
	}

	public void setNumeroNota(String numeroNota) {
		this.numeroNota = numeroNota;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getDescricaoObservacao() {
		return descricaoObservacao;
	}

	public void setDescricaoObservacao(String descricaoObservacao) {
		this.descricaoObservacao = descricaoObservacao;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public BigDecimal getValorIcms() {
		return valorIcms;
	}

	public void setValorIcms(BigDecimal valorIcms) {
		this.valorIcms = valorIcms;
	}

	public Date getDataCompra() {
		return dataCompra;
	}

	public void setDataCompra(Date dataCompra) {
		this.dataCompra = dataCompra;
	}

	public PessoaJuridicaModel getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaJuridicaModel pessoa) {
		this.pessoa = pessoa;
	}

	public ContaPagarModel getContaPagar() {
		return contaPagar;
	}

	public void setContaPagar(ContaPagarModel contaPagar) {
		this.contaPagar = contaPagar;
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
		NotaFiscalCompraModel other = (NotaFiscalCompraModel) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
