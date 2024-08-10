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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "PRODUTOS")
@SequenceGenerator(name = "SEQ_PRODUTOS", sequenceName = "SEQ_PRODUTOS", allocationSize = 1, initialValue = 1) 
public class Produtos implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRODUTOS")
    private Long id;
    
    @Column(nullable = false)
    private String TipoUnidade;
    
    @NotNull(message = "Descrição do produto deve ser informado.")
    @Size(min = 10, message = "O produto tem que ter no minimo 10 letras!")
    @Column(columnDefinition = "text", name = "DESCRICAO", nullable = false, length = 2000)
    private String Descricao;
    
    /*NOTA_ITEM_PRODUTO - ASOCCIAR*/
    
    @Column(nullable = false)
    private boolean Ativo = Boolean.TRUE;
    
    @NotNull(message = "O Peso do produto deve ser informado.")
    @Column(nullable = false)
    private double Peso;
    
    @NotNull(message = "A largura do produto deve ser informada.")
    @Column(nullable = false)
    private double Largura;
    
    @NotNull(message = "A altura do produto deve ser informada.")
    @Column(nullable = false)
    private double Altura;
    
    @NotNull(message = "A profundidade do produto deve ser informada.")
    @Column(nullable = false)
    private double Profundidade;
    
    @NotNull(message = "O valor de venda do produto deve ser informado.")
    @Column(nullable = false)
    private double ValorVenda;
    
    @Column(nullable = false)
    private double QtdeEstoque;
    
    @Column(nullable = false)
    private double QtdeAlertaEstoque;
    
    private String LinkYoutube;
    
    private boolean AlertaQtdeEstoque;
   
    private Integer QtdeClique;
    
    @NotNull(message = "A categoria do produto deve ser informada.")
    @ManyToOne(targetEntity = CategoriaProduto.class)
    @JoinColumn(name = "categoria_id", nullable = false,	 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_PRODUTOS_CATEGORIA_ID"))
    private CategoriaProduto categoriaProduto;    
    
    @NotNull(message = "A empresa responsável pelo produto deve ser informada.")
    @ManyToOne(targetEntity = Pessoas.class)
    @JoinColumn(name = "empresa_id", nullable = false, 
    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_PRODUTOS_EMP_ID"))
    private PessoaJuridicaModel Empresa;    
    
	@NotNull(message = "A marca do produto deve ser informada.")
	@ManyToOne(targetEntity = MarcaProdutoModel.class)
	@JoinColumn(name = "MARCA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_PRODUTOS_MARCA_ID"))
	private MarcaProdutoModel marcaProduto;
 
	public PessoaJuridicaModel getEmpresa() {
		return Empresa;
	}

	public void setEmpresa(PessoaJuridicaModel empresa) {
		this.Empresa = empresa;
	}

	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getTipoUnidade() {
		return TipoUnidade;
	}



	public void setTipoUnidade(String tipoUnidade) {
		TipoUnidade = tipoUnidade;
	}



	public String getDescricao() {
		return Descricao;
	}



	public void setDescricao(String descricao) {
		Descricao = descricao;
	}



	public boolean isAtivo() {
		return Ativo;
	}



	public void setAtivo(boolean ativo) {
		Ativo = ativo;
	}



	public double getPeso() {
		return Peso;
	}



	public void setPeso(double peso) {
		Peso = peso;
	}



	public double getLargura() {
		return Largura;
	}



	public void setLargura(double largura) {
		Largura = largura;
	}



	public double getAltura() {
		return Altura;
	}



	public void setAltura(double altura) {
		Altura = altura;
	}



	public double getProfundidade() {
		return Profundidade;
	}



	public void setProfundidade(double profundidade) {
		Profundidade = profundidade;
	}



	public double getValorVenda() {
		return ValorVenda;
	}



	public void setValorVenda(double valorVenda) {
		ValorVenda = valorVenda;
	}



	public double getQtdeEstoque() {
		return QtdeEstoque;
	}



	public void setQtdeEstoque(double qtdeEstoque) {
		QtdeEstoque = qtdeEstoque;
	}



	public double getQtdeAlertaEstoque() {
		return QtdeAlertaEstoque;
	}



	public void setQtdeAlertaEstoque(double qtdeAlertaEstoque) {
		QtdeAlertaEstoque = qtdeAlertaEstoque;
	}



	public String getLinkYoutube() {
		return LinkYoutube;
	}



	public void setLinkYoutube(String linkYoutube) {
		LinkYoutube = linkYoutube;
	}



	public boolean isAlertaQtdeEstoque() {
		return AlertaQtdeEstoque;
	}



	public void setAlertaQtdeEstoque(boolean alertaQtdeEstoque) {
		AlertaQtdeEstoque = alertaQtdeEstoque;
	}



	public Integer getQtdeClique() {
		return QtdeClique;
	}



	public void setQtdeClique(Integer qtdeClique) {
		QtdeClique = qtdeClique;
	}

	public CategoriaProduto getCategoriaProduto() {
		return categoriaProduto;
	}

	public void setCategoriaProduto(CategoriaProduto categoriaProduto) {
		this.categoriaProduto = categoriaProduto;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public MarcaProdutoModel getMarcaProduto() {
		return marcaProduto;
	}

	public void setMarcaProduto(MarcaProdutoModel marcaProduto) {
		this.marcaProduto = marcaProduto;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Produtos other = (Produtos) obj;
		return Objects.equals(id, other.id);
	}

}
