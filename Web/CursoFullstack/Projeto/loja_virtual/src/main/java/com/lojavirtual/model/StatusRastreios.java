package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "STATUS_RASTREIOS")
@SequenceGenerator(name = "SEQ_STATUS_RASTREIOS", sequenceName = "SEQ_STATUS_RASTREIOS", allocationSize = 1, initialValue = 1) 
public class StatusRastreios implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_STATUS_RASTREIOS")
    private Long id;
    
    private String CentroDistribuicao;
    private String Cidade;
    private String Estado;
    private String Status;
   

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCentroDistribuicao() {
		return CentroDistribuicao;
	}

	public void setCentroDistribuicao(String centroDistribuicao) {
		CentroDistribuicao = centroDistribuicao;
	}

	public String getCidade() {
		return Cidade;
	}

	public void setCidade(String cidade) {
		Cidade = cidade;
	}

	public String getEstado() {
		return Estado;
	}

	public void setEstado(String estado) {
		Estado = estado;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
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
		StatusRastreios other = (StatusRastreios) obj;
		return Objects.equals(id, other.id);
	}

	


}
