package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
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
@Table(name = "FORMAS_PAGAMENTOS")
@SequenceGenerator(name = "SEQ_FORMAS_PAGAMENTOS", sequenceName = "SEQ_FORMAS_PAGAMENTOS", allocationSize = 1, initialValue = 1) 
public class FormasPagamentos implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FORMAS_PAGAMENTOS")
    private Long id;
    
    @Column(nullable = false)
    private String Descricao;
    
    
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "empresa_id", nullable = false, 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_FORMAS_PAGAMENTOS_EMP_ID"))
    private Pessoas Empresa;    


	public Pessoas getEmpresa() {
		return Empresa;
	}

	public void setEmpresa(Pessoas empresa) {
		Empresa = empresa;
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
		FormasPagamentos other = (FormasPagamentos) obj;
		return Objects.equals(id, other.id);
	}

}
