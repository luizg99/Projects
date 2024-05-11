package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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

@jakarta.persistence.Entity
@Table(name = "CONTAS_RECEBER")
@SequenceGenerator(name = "SEQ_CONTAS_RECEBER", sequenceName = "SEQ_CONTAS_RECEBER", allocationSize = 1, initialValue = 1) 
public class ContasReceber implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONTAS_RECEBER")
    private Long id;
    
    @Column(nullable = false)
    private String Descricao;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusContasReceber Status;
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date Vencimento;

    @Temporal(TemporalType.DATE)
    private Date DataPagamento;
    
    @Column(nullable = false)
    private Double ValorTotal;
    
    private Double ValorDesconto;
       
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CONTAS_RECEBER_PESSOA_ID"))
    private Pessoas Pessoa;

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

	public StatusContasReceber getStatus() {
		return Status;
	}

	public void setStatus(StatusContasReceber status) {
		Status = status;
	}

	public Date getVencimento() {
		return Vencimento;
	}

	public void setVencimento(Date vencimento) {
		Vencimento = vencimento;
	}

	public Date getDataPagamento() {
		return DataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		DataPagamento = dataPagamento;
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

	public Pessoas getPessoa() {
		return Pessoa;
	}

	public void setPessoa(Pessoas pessoa) {
		Pessoa = pessoa;
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
		ContasReceber other = (ContasReceber) obj;
		return Objects.equals(id, other.id);
	}
    
   
}