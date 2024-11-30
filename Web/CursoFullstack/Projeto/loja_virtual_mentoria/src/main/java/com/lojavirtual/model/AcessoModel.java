package com.lojavirtual.model;

import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "ACESSOS")
@SequenceGenerator(name = "SEQ_ACESSOS", sequenceName = "SEQ_ACESSOS", allocationSize = 1, initialValue = 1) 
public class AcessoModel implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ACESSOS")
    private Long id;

    private String descricao;
    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(nullable = false)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
		AcessoModel other = (AcessoModel) obj;
		return Objects.equals(id, other.id);
	}

	@Override
    @JsonIgnore
	public String getAuthority() {
		return this.descricao;
	}
}
