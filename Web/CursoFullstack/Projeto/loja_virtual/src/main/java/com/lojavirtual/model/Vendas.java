package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.hibernate.sql.model.jdbc.DeleteOrUpsertOperation;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.criteria.CriteriaBuilder.In;

@Entity
@Table(name = "VENDAS")
@SequenceGenerator(name = "SEQ_VENDAS", sequenceName = "SEQ_VENDAS", allocationSize = 1, initialValue = 1)
public class Vendas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_VENDAS")
	private Long id;

	@ManyToOne(targetEntity = Pessoas.class)
	@JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_PESSOA_ID"))
	private Pessoas Pessoa;

	@ManyToOne(targetEntity = Enderecos.class)
	@JoinColumn(name = "ENDERECO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_ENDERECO_ID"))
	private Enderecos Endereco;

	@ManyToOne(targetEntity = Enderecos.class)
	@JoinColumn(name = "ENDERECO_COBRANCA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_ENDERECO_COBRANCA_ID"))
	private Enderecos EnderecoCobranca;

	@Column(nullable = false)
	private double ValorTotal;
	
	private double ValorDesconto;

	@ManyToOne(targetEntity = FormasPagamentos.class)
	@JoinColumn(name = "FORMA_PAGAMENTO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_ENDERECO_COBRANCA_ID"))
	private FormasPagamentos FormaPagamento;

	@OneToOne(targetEntity = NotasFiscaisVendas.class)
	@JoinColumn(name = "NOTA_FISCAL_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_NOTA_FISCAL_ID"))
	private NotasFiscaisVendas NotaFiscalVenda;

	@ManyToOne(targetEntity = CuponsDescontos.class)
	@JoinColumn(name = "CUPOM_FISCAL_ID", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_CUPOM_FISCAL_ID"))
	private CuponsDescontos CupomFiscal;

	@Column(nullable = false)
	private double ValorFrete;
	
	@Column(nullable = false)
	private Integer DiasEntrega;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date DataVenda;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date DataEntrega;

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

	public Pessoas getPessoa() {
		return Pessoa;
	}

	public void setPessoa(Pessoas pessoa) {
		Pessoa = pessoa;
	}

	public Enderecos getEndereco() {
		return Endereco;
	}

	public void setEndereco(Enderecos endereco) {
		Endereco = endereco;
	}

	public Enderecos getEnderecoCobranca() {
		return EnderecoCobranca;
	}

	public void setEnderecoCobranca(Enderecos enderecoCobranca) {
		EnderecoCobranca = enderecoCobranca;
	}

	public double getValorTotal() {
		return ValorTotal;
	}

	public void setValorTotal(double valorTotal) {
		ValorTotal = valorTotal;
	}

	public double getValorDesconto() {
		return ValorDesconto;
	}

	public void setValorDesconto(double valorDesconto) {
		ValorDesconto = valorDesconto;
	}

	public FormasPagamentos getFormaPagamento() {
		return FormaPagamento;
	}

	public void setFormaPagamento(FormasPagamentos formaPagamento) {
		FormaPagamento = formaPagamento;
	}

	public NotasFiscaisVendas getNotaFiscalVenda() {
		return NotaFiscalVenda;
	}

	public void setNotaFiscalVenda(NotasFiscaisVendas notaFiscalVenda) {
		NotaFiscalVenda = notaFiscalVenda;
	}

	public CuponsDescontos getCupomFiscal() {
		return CupomFiscal;
	}

	public void setCupomFiscal(CuponsDescontos cupomFiscal) {
		CupomFiscal = cupomFiscal;
	}

	public double getValorFrete() {
		return ValorFrete;
	}

	public void setValorFrete(double valorFrete) {
		ValorFrete = valorFrete;
	}

	public Integer getDiasEntrega() {
		return DiasEntrega;
	}

	public void setDiasEntrega(Integer diasEntrega) {
		DiasEntrega = diasEntrega;
	}

	public Date getDataVenda() {
		return DataVenda;
	}

	public void setDataVenda(Date dataVenda) {
		DataVenda = dataVenda;
	}

	public Date getDataEntrega() {
		return DataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		DataEntrega = dataEntrega;
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
		Vendas other = (Vendas) obj;
		return Objects.equals(id, other.id);
	}

}
