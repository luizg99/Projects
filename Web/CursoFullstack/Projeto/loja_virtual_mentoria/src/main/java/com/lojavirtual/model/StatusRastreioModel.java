package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Entity
@Table(name = "STATUS_RASTREIOS")
@SequenceGenerator(name = "SEQ_STATUS_RASTREIOS", sequenceName = "SEQ_STATUS_RASTREIOS", allocationSize = 1, initialValue = 1) 
public class StatusRastreioModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_STATUS_RASTREIOS")
    private Long id;
	
	private String urlRastreio;

	private String centroDistribuicao;
	
	private String cidade;
	
	private String estado;
	
	private String status;
	
	@JsonIgnore
	@ManyToOne(targetEntity = VendaModel.class)
	@JoinColumn(name = "VENDA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_STATUS_RASTRE_VENDA_ID"))
	private VendaModel venda;
	
	@JsonIgnore
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_STATUS_RASTRE_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public String getUrlRastreio() {
		return urlRastreio;
	}

	public void setUrlRastreio(String urlRastreio) {
		this.urlRastreio = urlRastreio;
	}
    
	public String getCentroDistribuicao() {
		return centroDistribuicao;
	}

	public void setCentroDistribuicao(String centroDistribuicao) {
		this.centroDistribuicao = centroDistribuicao;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public PessoaJuridicaModel getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(PessoaJuridicaModel empresa) {
		this.empresa = empresa;
	}

	public void setVenda(VendaModel venda) {
		this.venda = venda;
	}

	public VendaModel getVenda() {
		return venda;
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
		StatusRastreioModel other = (StatusRastreioModel) obj;
		return Objects.equals(id, other.id);
	}
}

