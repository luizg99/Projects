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
@Table(name = "CUPONS_DESCONTOS")
@SequenceGenerator(name = "SEQ_CUPONS_DESC", sequenceName = "SEQ_CUPONS_DESC", allocationSize = 1, initialValue = 1) 
public class CupomDescontoModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CUPONS_DESC")
    private Long id;

	@NotNull(message = "Codigo de descrição do cupom deve ser preenchido.")
	@Column(nullable = false)
	private String codigoDescricao;
	
	private BigDecimal valorRealDesconto;
	
	private BigDecimal valorPercentualDesconto;

	@NotNull(message = "A data de validade do cupom deve ser preenchida.")
	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataValidade;
	
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CUPOM_DESC_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;
		
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public String getCodigoDescricao() {
		return codigoDescricao;
	}

	public void setCodigoDescricao(String codigoDescricao) {
		this.codigoDescricao = codigoDescricao;
	}

	public BigDecimal getValorRealDesconto() {
		return valorRealDesconto;
	}

	public void setValorRealDesconto(BigDecimal valorRealDesconto) {
		this.valorRealDesconto = valorRealDesconto;
	}

	public BigDecimal getValorPercentualDesconto() {
		return valorPercentualDesconto;
	}

	public void setValorPercentualDesconto(BigDecimal valorPercentualDesconto) {
		this.valorPercentualDesconto = valorPercentualDesconto;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
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
		CupomDescontoModel other = (CupomDescontoModel) obj;
		return Objects.equals(id, other.id);
	}
}

