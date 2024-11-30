package com.lojavirtual.model;

import java.util.Objects;

import org.hibernate.bytecode.enhance.spi.EnhancementInfo;
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
public class Acessos  implements GrantedAuthority{
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ACESSOS")
    private Long id;
    
    @Column(name = "DESCRICAO", nullable = false)
    private String Descricao;
    
    @JsonIgnore
	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return null;
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
		Acessos other = (Acessos) obj;
		return Objects.equals(id, other.id);
	}
    

}
