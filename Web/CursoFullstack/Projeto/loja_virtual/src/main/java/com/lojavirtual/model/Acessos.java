package com.lojavirtual.model;

import org.hibernate.bytecode.enhance.spi.EnhancementInfo;
import org.springframework.security.core.GrantedAuthority;

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
    private String descricao;

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
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
    

}
