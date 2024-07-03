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
@Table(name = "CUPONS_DESCONTOS")
@SequenceGenerator(name = "SEQ_CUPONS_DESCONTOS", sequenceName = "SEQ_CUPONS_DESCONTOS", allocationSize = 1, initialValue = 1) 
public class CuponsDescontos implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CUPONS_DESCONTOS")
    private Long id;
    
    @Column(name = "DESCRICAO", nullable = false)
    private String Descricao;
    
    @Column(nullable = false)
    private String CodigoDesconto;
    
    private Double ValorRealDesc;
    private Double ValorPercDesc;
    
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date DataValidadeCupom;
    
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "empresa_id", nullable = false, 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_CUPONS_DESCONTOS_EMP_ID"))
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

	public String getDescricao() {
		return Descricao;
	}

	public void setDescricao(String descricao) {
		Descricao = descricao;
	}

	public String getCodigoDesconto() {
		return CodigoDesconto;
	}

	public void setCodigoDesconto(String codigoDesconto) {
		CodigoDesconto = codigoDesconto;
	}

	public Double getValorRealDesc() {
		return ValorRealDesc;
	}

	public void setValorRealDesc(Double valorRealDesc) {
		ValorRealDesc = valorRealDesc;
	}

	public Double getValorPercDesc() {
		return ValorPercDesc;
	}

	public void setValorPercDesc(Double valorPercDesc) {
		ValorPercDesc = valorPercDesc;
	}

	public Date getDataValidadeCupom() {
		return DataValidadeCupom;
	}

	public void setDataValidadeCupom(Date dataValidadeCupom) {
		DataValidadeCupom = dataValidadeCupom;
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
		CuponsDescontos other = (CuponsDescontos) obj;
		return Objects.equals(id, other.id);
	}	


}
