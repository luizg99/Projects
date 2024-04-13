package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "USUARIOS")
@SequenceGenerator(name = "SEQ_USUARIOS", sequenceName = "SEQ_USUARIOS", allocationSize = 1, initialValue = 1) 
public class Usuarios implements UserDetails{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USUARIOS")
	private long id;
	
	private String login;
	private String senha;
	
	@Temporal(TemporalType.DATE)
	private Date data_atual_senha;
	
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "usuarios_acessos",
		uniqueConstraints = @UniqueConstraint(columnNames = { "usuario_id", "acesso_id"},
		name = "unque_acesso_user"),
		joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id", table = "usuarios", unique = false, foreignKey = @ForeignKey(name = "usuario_fk", value = ConstraintMode.CONSTRAINT)),
		inverseJoinColumns = @JoinColumn(name = "acesso_id", unique = false, referencedColumnName = "id", table = "acessos"),
		foreignKey = @ForeignKey(name = "acesso_fk", value = ConstraintMode.CONSTRAINT)
	)
	private List<Acessos> acessos;
	
	
	private static final long serialVersionUID = 1L;

	/*Autoridades = os acessos, ou seja ROLE_ADMIN, ROLE_FINANCEIRO, ETC*/
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.acessos;
	}

	@Override
	public String getPassword() {
		return this.senha;
	}

	@Override
	public String getUsername() {
		return this.login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() { 
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	
}
