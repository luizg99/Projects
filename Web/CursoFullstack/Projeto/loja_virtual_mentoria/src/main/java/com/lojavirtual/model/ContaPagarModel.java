package com.lojavirtual.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import com.lojavirtual.enums.StatusContaPagar;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "CONTAS_PAGAR")
@SequenceGenerator(name = "SEQ_CONTAS_PAGAR", sequenceName = "SEQ_CONTAS_PAGAR", allocationSize = 1, initialValue = 1)
public class ContaPagarModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONTAS_PAGAR")
	private Long id;

	@NotEmpty(message = "Informe o campo descrição da conta a pagar")
	@NotNull(message = "Informe o campo descrição da conta a pagar")
	@Column(nullable = false)
	private String descricao;

	@NotNull(message = "Informe a data de vencimento da conta a pagar")
	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;

	@Temporal(TemporalType.DATE)
	private Date dataPagamento;

	@NotNull(message = "Informe o valor total da conta a pagar")
	@Column(nullable = false)
	private BigDecimal valorTotal;

	private BigDecimal valorDesconto;

	@ManyToOne(targetEntity = PessoaFisicaModel.class)
	@JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CONTAS_PAG_PESSOA_ID"))
	private PessoaFisicaModel pessoa;
	
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "FORNECEDOR_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CONTAS_PAG_FORNECEDOR_ID"))
	private PessoaJuridicaModel fornecedor;

	@NotNull(message = "Informe o status da conta a pagar")
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private StatusContaPagar status;

	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CONTAS_PAG_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;
		
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PessoaFisicaModel getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaFisicaModel pessoa) {
		this.pessoa = pessoa;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
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

	public StatusContaPagar getStatus() {
		return status;
	}

	public void setStatus(StatusContaPagar status) {
		this.status = status;
	}
	
	public PessoaJuridicaModel getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(PessoaJuridicaModel fornecedor) {
		this.fornecedor = fornecedor;
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
		ContaPagarModel other = (ContaPagarModel) obj;
		return Objects.equals(id, other.id);
	}

}
