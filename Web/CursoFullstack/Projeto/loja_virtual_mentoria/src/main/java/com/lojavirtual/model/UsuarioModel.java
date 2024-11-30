package com.lojavirtual.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lojavirtual.model.dto.LoginRequest;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "USUARIOS")
@SequenceGenerator(name = "SEQ_USUARIOS", sequenceName = "SEQ_USUARIOS", allocationSize = 1, initialValue = 1) 
public class UsuarioModel implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USUARIOS")
    private Long id;
	
	@Column(nullable = false, unique = true)
	private String login;
	
	@Column(nullable = false)
	private String senha;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
    private Date dataAtualSenha;
	
	@ManyToOne(targetEntity = PessoaModel.class)
	@JoinColumn(name = "PESSOA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_USUARIOS_PESSOA_ID"))
	private PessoaModel pessoa;
	
	@ManyToOne(targetEntity = PessoaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_USUARIOS_EMPRESA_ID"))
	private PessoaModel empresa;
	
	public PessoaModel getEmpresa() {
		return empresa;
	}

	public void setEmpresa(PessoaModel empresa) {
		this.empresa = empresa;
	}

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "USUARIOS_ACESSOS", 
		uniqueConstraints = @UniqueConstraint (columnNames = {"usuario_id", "acesso_id"}, name = "uni_acesso_usuario" ), 
		joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id", table = "usuarios", unique = false, foreignKey = @ForeignKey(name = "fk_usuario_id", value = ConstraintMode.CONSTRAINT)),
		inverseJoinColumns = @JoinColumn(name = "acesso_id", referencedColumnName = "id", table = "acessos", unique = false, foreignKey = @ForeignKey(name = "fk_acesso_id", value = ConstraintMode.CONSTRAINT))
	)
	private List<AcessoModel> acessos;
    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public PessoaModel getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaModel pessoa) {
		this.pessoa = pessoa;
	}

	/* AUTORIDADES = ACESSO, ROLE_ADMIN, ROLE_SECRETARIO, ETC */
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Date getDataAtualSenha() {
		return dataAtualSenha;
	}

	public void setDataAtualSenha(Date dataAtualSenha) {
		this.dataAtualSenha = dataAtualSenha;
	}

	public List<AcessoModel> getAcessos() {
		return acessos;
	}

	public void setAcessos(List<AcessoModel> acessos) {
		this.acessos = acessos;
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
	
	public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder passwordEncoder) {
	    return passwordEncoder.matches(loginRequest.senha(), this.senha);
	}

}