package com.lojavirtual.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "PESSOAS")
@SequenceGenerator(name = "SEQ_PESSOAS", sequenceName = "SEQ_PESSOAS", allocationSize = 1, initialValue = 1)
public abstract class PessoaModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PESSOAS")
	private Long id;
	
	@Size(min = 3, message = "Nome deve ter pelo menos 3 letras")
	@NotBlank(message = "Nome deve ser informado")
	@NotNull(message = "Nome deve ser informado")
	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String telefone;
	
	@Column
	private String tipoPessoa; 

	@Email
	@Column(nullable = false)
	private String email;
	
	@OneToMany(mappedBy = "pessoa", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<EnderecoModel> enderecos = new ArrayList<EnderecoModel>();
	
	@ManyToOne(targetEntity = PessoaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_PESSOAS_EMPRESA_ID"))
	private PessoaModel empresa;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public List<EnderecoModel> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<EnderecoModel> enderecos) {
		this.enderecos = enderecos;
	}
	
	public PessoaModel getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(PessoaModel empresa) {
		this.empresa = empresa;
	}
	
	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
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
		PessoaModel other = (PessoaModel) obj;
		return Objects.equals(id, other.id);
	}
	
}
