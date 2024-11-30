package com.lojavirtual.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SequenceGenerator(name = "SEQ_PESSOAS", sequenceName = "SEQ_PESSOAS", allocationSize = 1, initialValue = 1) 
public abstract class Pessoas implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PESSOAS")
    private Long id;
    
    @Size(min = 4, message = ("O nome deve ter no minimo 4 caracteres"))
    @NotBlank(message = "Nome deve ser informado")
    @NotNull(message = "Não pode ser null")
    @Column(nullable = false)
    private String Nome;
    
    @jakarta.validation.constraints.Email
    @Column(nullable = false)
    private String Email;
    
    @Column(nullable = false)
    private String Telefone;
    
    @Column
    private String tipo_pessoa;
    
    @OneToMany(mappedBy = "Pessoa", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)     
    private List<Enderecos> Enderecos = new ArrayList<Enderecos>();
    
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "empresa_id", nullable = true, 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_EMPRESA_ID"))
    private Pessoas empresa;    

	public Pessoas getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoas empresa) {
		this.empresa = empresa;
	}

	public String getTipo_pessoa() {
		return tipo_pessoa;
	}

	public void setTipo_pessoa(String tipo_pessoa) {
		this.tipo_pessoa = tipo_pessoa;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return Nome;
	}

	public void setNome(String nome) {
		Nome = nome;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getTelefone() {
		return Telefone;
	}

	public void setTelefone(String telefone) {
		Telefone = telefone;
	}

	public List<Enderecos> getEnderecos() {
		return Enderecos;
	}

	public void setEnderecos(List<Enderecos> enderecos) {
		Enderecos = enderecos;
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
		Pessoas other = (Pessoas) obj;
		return Objects.equals(id, other.id);
	}   
       
    
}
