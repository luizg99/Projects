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
@Table(name = "IMAGENS_PRODUTOS")
@SequenceGenerator(name = "SEQ_IMAGENS_PRODUTOS", sequenceName = "SEQ_IMAGENS_PRODUTOS", allocationSize = 1, initialValue = 1) 
public class ImagensProdutos implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_IMAGENS_PRODUTOS")
    private Long id;
  
    @Column(columnDefinition = "text", nullable = false)
	private String ImagemOriginal;
    
    @Column(columnDefinition = "text", nullable = false)
	private String ImagemMiniatura;
	

    @ManyToOne(targetEntity = Produtos.class)
    @JoinColumn(name = "PRODUTO_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_IMAGENS_PRODUTOS_PRODUTO_ID"))
    private Produtos Produto;
  

    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "empresa_id", nullable = false, 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_EMPRESA_ID"))
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




	public String getImagemOriginal() {
		return ImagemOriginal;
	}




	public void setImagemOriginal(String imagemOriginal) {
		ImagemOriginal = imagemOriginal;
	}




	public String getImagemMiniatura() {
		return ImagemMiniatura;
	}




	public void setImagemMiniatura(String imagemMiniatura) {
		ImagemMiniatura = imagemMiniatura;
	}




	public Produtos getProduto() {
		return Produto;
	}




	public void setProduto(Produtos produto) {
		Produto = produto;
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImagensProdutos other = (ImagensProdutos) obj;
		return Objects.equals(id, other.id);
	}

}
