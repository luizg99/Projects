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
public class NotasFiscaisVendas implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTAS_FISCAIS_VENDAS")
    private Long id;
    
    @Column(nullable = false)
    private String Numero; 
    
    @Column(nullable = false)
    private String Serie;
    
    @Column(nullable = false)
    private String Tipo;
    
    @Column(columnDefinition = "text", nullable = false)
    private String Xml;
    
    @Column(columnDefinition = "text", nullable = false)
    private String Pdf;
    
    @OneToOne(targetEntity = Vendas.class)
    @JoinColumn(name = "VENDA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_NOTAS_FISCAIS_VENDAS_VEN_ID"))
    private Vendas Venda;
    
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
	public String getTipo() {
		return Tipo;
	}
	public void setTipo(String tipo) {
		Tipo = tipo;
	}
	public String getXml() {
		return Xml;
	}
	public void setXml(String xml) {
		Xml = xml;
	}
	public String getPdf() {
		return Pdf;
	}
	public void setPdf(String pdf) {
		Pdf = pdf;
	}
	public Vendas getVenda() {
		return Venda;
	}
	public void setVenda(Vendas venda) {
		Venda = venda;
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
		NotasFiscaisVendas other = (NotasFiscaisVendas) obj;
		return Objects.equals(id, other.id);
	}

	


}
