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
@Table(name = "NOTAS_FISCAIS_VENDAS")
@SequenceGenerator(name = "SEQ_NOTAS_FISCAIS_VENDAS", sequenceName = "SEQ_NOTAS_FISCAIS_VENDAS", allocationSize = 1, initialValue = 1)
public class NotaFiscalVendaModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTAS_FISCAIS_VENDAS")
	private Long id;

	@Column(nullable = false)
	private String numero;

	@Column(nullable = false)
	private String serie;

	@Column(nullable = false)
	private String tipo;

	@Column(columnDefinition = "text", nullable = false)
	private String xml;

	@Column(columnDefinition = "text")
	private String pdf;

	@OneToOne
	@JoinColumn(name = "VENDA_ID", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NF_VENDA_VENDA_ID"))
	private VendaModel venda;

	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NF_VENDA_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public VendaModel getVenda() {
		return venda;
	}

	public void setVenda(VendaModel venda) {
		this.venda = venda;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
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
		NotaFiscalVendaModel other = (NotaFiscalVendaModel) obj;
		return Objects.equals(id, other.id);
	}
}
