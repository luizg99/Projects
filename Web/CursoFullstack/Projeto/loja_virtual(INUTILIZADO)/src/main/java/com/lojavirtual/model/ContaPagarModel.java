package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.lojavirtual.enums.StatusContasPagar;
import com.lojavirtual.enums.StatusContasReceber;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
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

@jakarta.persistence.Entity
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
    private String Descricao;
    
    @Column(nullable = false)
    private Double ValorTotal;
    
    private Double ValorDesconto;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusContasPagar Status;
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date DataVencimento;

    @Temporal(TemporalType.DATE)
    private Date DataPagamento;
       
    @ManyToOne(targetEntity = PessoaFisicaModel.class)
	@JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CONTAS_PAG_PESSOA_ID"))
	private PessoaFisicaModel pessoa;
    
    @ManyToOne(targetEntity = PessoaJuridicaModel .class)
    @JoinColumn(name = "forn_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CON_PAG_FORN_ID"))
    private PessoaJuridicaModel  PessoaFornecedor;
    
    @ManyToOne(targetEntity = PessoaJuridicaModel.class)
    @JoinColumn(name = "empresa_id", nullable = false, 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CONTAS_PAGAR_EMP_ID"))
    private PessoaJuridicaModel Empresa;    


	public PessoaJuridicaModel getEmpresa() {
		return Empresa;
	}

	public void setEmpresa(PessoaJuridicaModel empresa) {
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
		Descricao = descricao;
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

	public StatusContasPagar getStatus() {
		return Status;
	}

	public void setStatus(StatusContasPagar status) {
		Status = status;
	}

	public Date getDataVencimento() {
		return DataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		DataVencimento = dataVencimento;
	}

	public Date getDataPagamento() {
		return DataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		DataPagamento = dataPagamento;
	}

	public PessoaFisicaModel getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaFisicaModel pessoa) {
		this.pessoa = pessoa;
	}

	public PessoaJuridicaModel getPessoaFornecedor() {
		return PessoaFornecedor;
	}

	public void setPessoaFornecedor(PessoaJuridicaModel pessoaFornecedor) {
		PessoaFornecedor = pessoaFornecedor;
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