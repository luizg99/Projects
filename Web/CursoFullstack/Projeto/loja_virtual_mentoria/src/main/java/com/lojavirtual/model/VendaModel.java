package com.lojavirtual.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "VENDAS")
@SequenceGenerator(name = "SEQ_VENDAS", sequenceName = "SEQ_VENDAS", allocationSize = 1, initialValue = 1) 
public class VendaModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_VENDAS")
    private Long id;
	
	@NotNull(message = "O cliente deve ser informado.")
	@ManyToOne(targetEntity = PessoaFisicaModel.class)
	@JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_PESSOA_ID"))
	private PessoaFisicaModel pessoa;
	
	@NotNull(message = "O endereço de entrega deve ser informado.")
	@ManyToOne(targetEntity = EnderecoModel.class)
	@JoinColumn(name = "ENDERECO_ENTREGA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_END_ENTRE_ID"))
	private EnderecoModel enderecoEntrega;

	@NotNull(message = "O endereço de cobrança deve ser informado.")
	@ManyToOne(targetEntity = EnderecoModel.class)
	@JoinColumn(name = "ENDERECO_COBRANCA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_END_COBRA_ID"))
	private EnderecoModel enderecoCobranca;
	
	@Min(value = 1, message = "Valor total da venda é invalida.")
	@Column(nullable = false)
	private BigDecimal valorTotal;
	
	private BigDecimal valorDesconto;
	
	@NotNull(message = "A forma de pagamento deve ser informada.")
	@ManyToOne(targetEntity = FormaPagamentoModel.class)
	@JoinColumn(name = "FORMA_PAGAMENTO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_FORMA_PAG_ID"))
	private FormaPagamentoModel formaPagamento;
	
	@JsonIgnoreProperties(allowGetters = true)
	@NotNull(message = "A nota fiscal deve ser informada")
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "NOTA_FISCAL_ID", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_NOTA_FISCAL_ID"))
	private NotaFiscalVendaModel notaFiscalVenda;
	
	@ManyToOne(targetEntity = CupomDescontoModel.class)
	@JoinColumn(name = "CUPOM_DESCONTO_ID", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_CUPOM_DESC_ID"))
	private CupomDescontoModel cupomDesconto;
	
	@NotNull(message = "O valor do frete deve ser informado.")
	@Column(nullable = false)
	private BigDecimal valorFrete;
	
	@Min(value = 1, message = "Dia de entrega está inválido.")
	@Column(nullable = false)
	private Integer diasEntrega;
	
	@NotNull(message = "A data da venda deve ser informada.")
	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date dataVenda;
	
	@Temporal(TemporalType.DATE)
	private Date dataEntrega;
	
	@NotNull(message = "A empresa deve ser informada.")
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_VENDAS_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;

	@OneToMany(mappedBy = "venda", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<VendaItemModel> itens = new ArrayList<VendaItemModel>();

	private Boolean excluido = Boolean.FALSE;
		
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getExcluido() {
		return excluido;
	}
	
	public void setExcluido(Boolean excluido) {
		this.excluido = excluido;
	}

	public PessoaFisicaModel getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaFisicaModel pessoa) {
		this.pessoa = pessoa;
	}

	public EnderecoModel getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public void setEnderecoEntrega(EnderecoModel enderecoEntrega) {
		this.enderecoEntrega = enderecoEntrega;
	}

	public EnderecoModel getEnderecoCobranca() {
		return enderecoCobranca;
	}

	public void setEnderecoCobranca(EnderecoModel enderecoCobranca) {
		this.enderecoCobranca = enderecoCobranca;
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

	public FormaPagamentoModel getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamentoModel formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public NotaFiscalVendaModel getNotaFiscalVenda() {
		return notaFiscalVenda;
	}

	public void setNotaFiscalVenda(NotaFiscalVendaModel notaFiscalVenda) {
		this.notaFiscalVenda = notaFiscalVenda;
	}

	public CupomDescontoModel getCupomDesconto() {
		return cupomDesconto;
	}

	public void setCupomDesconto(CupomDescontoModel cupomDesconto) {
		this.cupomDesconto = cupomDesconto;
	}

	public BigDecimal getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public Integer getDiasEntrega() {
		return diasEntrega;
	}

	public void setDiasEntrega(Integer diasEntrega) {
		this.diasEntrega = diasEntrega;
	}

	public Date getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(Date dataVenda) {
		this.dataVenda = dataVenda;
	}

	public Date getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}
	
	public PessoaJuridicaModel getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(PessoaJuridicaModel empresa) {
		this.empresa = empresa;
	}

	public void setItens(List<VendaItemModel> itens) {
		this.itens = itens;
	}

	public List<VendaItemModel> getItens() {
		return itens;
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
		VendaModel other = (VendaModel) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
