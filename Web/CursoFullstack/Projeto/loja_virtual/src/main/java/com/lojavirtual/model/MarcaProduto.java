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

@jakarta.persistence.Entity
@Table(name = "MARCAS_PRODUTOS")
@SequenceGenerator(name = "SEQ_MARCAS_PRODUTOS", sequenceName = "SEQ_MARCAS_PRODUTOS", allocationSize = 1, initialValue = 1) 
public class MarcaProduto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MARCAS_PRODUTOS")
    private Long id;
    
    @Column(nullable = false)
    private String Descricao;

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
        this.Descricao = descricao;
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
		MarcaProduto other = (MarcaProduto) obj;
		return Objects.equals(id, other.id);
	}


}