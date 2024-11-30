package com.lojavirtual.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "IMAGENS_PRODUTOS")
@SequenceGenerator(name = "SEQ_IMAGENS_PRODUTOS", sequenceName = "SEQ_IMAGENS_PRODUTOS", allocationSize = 1, initialValue = 1) 
public class ImagemProdutoModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_IMAGENS_PRODUTOS")
    private Long id;

	@Column(columnDefinition = "text", nullable = false)
    private String imagemOriginal;
	
	@Column(columnDefinition = "text", nullable = false)
    private String imagemMiniatura;
	
	@JsonIgnoreProperties(allowGetters = true)
	@ManyToOne(targetEntity = ProdutoModel.class)
	@JoinColumn(name = "PRODUTO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_IMAG_PRODUTO_PRODUTO_ID"))
	private ProdutoModel produto;
	
	@JsonIgnoreProperties(allowGetters = true)
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_IMAG_PRODUTO_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;
	    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
   
	public String getImagemOriginal() {
		return imagemOriginal;
	}

	public void setImagemOriginal(String imagemOriginal) {
		this.imagemOriginal = imagemOriginal;
	}

	public String getImagemMiniatura() {
		return imagemMiniatura;
	}

	public void setImagemMiniatura(String imagemMiniatura) {
		this.imagemMiniatura = imagemMiniatura;
	}

	public ProdutoModel getProduto() {
		return produto;
	}

	public void setProduto(ProdutoModel produto) {
		this.produto = produto;
	}
	
	public PessoaJuridicaModel getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(PessoaJuridicaModel empresa) {
		this.empresa = empresa;
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
		ImagemProdutoModel other = (ImagemProdutoModel) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
