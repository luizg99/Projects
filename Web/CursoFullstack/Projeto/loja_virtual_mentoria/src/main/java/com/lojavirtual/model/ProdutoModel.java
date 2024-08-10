package com.lojavirtual.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "PRODUTOS")
@SequenceGenerator(name = "SEQ_PRODUTOS", sequenceName = "SEQ_PRODUTOS", allocationSize = 1, initialValue = 1) 
public class ProdutoModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRODUTOS")
    private Long id;
	
	@NotNull(message = "O tipo da unidade deve ser informado.")
	@Column(nullable = false)
	private String tipoUnidade;
	
	@NotNull(message = "Nome do produto deve ser informado.")
	@Size(min = 10, message = "Nome do produto deve ter mais de 10 letras.")
	@Column(nullable = false)
	private String nome;
	
	private Boolean ativo = Boolean.TRUE;

	@NotNull(message = "Descrição do produto deve ser informado.")
	@Column(columnDefinition = "text", length = 2000)
	private String descricao;
	
	/** NOTA ITEM PRODUTO - ASSOCIAR **/
	
	@NotNull(message = "O Peso do produto deve ser informado.")
	@Column(nullable = false)
	private Double peso;
	
	@NotNull(message = "A largura do produto deve ser informada.")
	@Column(nullable = false)
	private Double largura;
	
	@NotNull(message = "A altura do produto deve ser informada.")
	@Column(nullable = false)
	private Double altura;
	
	@NotNull(message = "A profundidade do produto deve ser informada.")
	@Column(nullable = false)
	private Double profundidade;
	
	@NotNull(message = "O valor de venda do produto deve ser informado.")
	@Column(nullable = false)
	private BigDecimal valorVenda = BigDecimal.ZERO;
	
	@Column(nullable = false)
	private Integer quantidadeEstoque = 0;
	
	private Integer quantidadeAlertaEstoque = 0;
	
	private String linkYoutube;
	
	private Boolean alertaQuantidadeEstoque = Boolean.FALSE;
	
	private Integer quantidadeClique = 0;
	
	@NotNull(message = "A empresa responsável pelo produto deve ser informada.")
	@ManyToOne(targetEntity = PessoaJuridicaModel.class)
	@JoinColumn(name = "EMPRESA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_PRODUTOS_EMPRESA_ID"))
	private PessoaJuridicaModel empresa;
	
	@NotNull(message = "A categoria do produto deve ser informada.")
	@ManyToOne(targetEntity = CategoriaProdutoModel.class)
	@JoinColumn(name = "CATEGORIA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_PRODUTOS_CATEGORIA_ID"))
	private CategoriaProdutoModel categoriaProduto;
	
	@NotNull(message = "A marca do produto deve ser informada.")
	@ManyToOne(targetEntity = MarcaProdutoModel.class)
	@JoinColumn(name = "MARCA_ID", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_PRODUTOS_MARCA_ID"))
	private MarcaProdutoModel marcaProduto;
	
	@OneToMany(mappedBy = "produto", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ImagemProdutoModel> imagens = new ArrayList<ImagemProdutoModel>();
	
		
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public String getTipoUnidade() {
		return tipoUnidade;
	}

	public void setTipoUnidade(String tipoUnidade) {
		this.tipoUnidade = tipoUnidade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}

	public Double getLargura() {
		return largura;
	}

	public void setLargura(Double largura) {
		this.largura = largura;
	}

	public Double getAltura() {
		return altura;
	}

	public void setAltura(Double altura) {
		this.altura = altura;
	}

	public Double getProfundidade() {
		return profundidade;
	}

	public void setProfundidade(Double profundidade) {
		this.profundidade = profundidade;
	}

	public BigDecimal getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(BigDecimal valorVenda) {
		this.valorVenda = valorVenda;
	}

	public Integer getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(Integer quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}

	public Integer getQuantidadeAlertaEstoque() {
		return quantidadeAlertaEstoque;
	}

	public void setQuantidadeAlertaEstoque(Integer quantidadeAlertaEstoque) {
		this.quantidadeAlertaEstoque = quantidadeAlertaEstoque;
	}

	public String getLinkYoutube() {
		return linkYoutube;
	}

	public void setLinkYoutube(String linkYoutube) {
		this.linkYoutube = linkYoutube;
	}

	public Boolean getAlertaQuantidadeEstoque() {
		return alertaQuantidadeEstoque;
	}

	public void setAlertaQuantidadeEstoque(Boolean alertaQuantidadeEstoque) {
		this.alertaQuantidadeEstoque = alertaQuantidadeEstoque;
	}

	public Integer getQuantidadeClique() {
		return quantidadeClique;
	}

	public void setQuantidadeClique(Integer quantidadeClique) {
		this.quantidadeClique = quantidadeClique;
	}
	
	public PessoaJuridicaModel getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(PessoaJuridicaModel empresa) {
		this.empresa = empresa;
	}
	
	public CategoriaProdutoModel getCategoriaProduto() {
		return categoriaProduto;
	}

	public void setCategoriaProduto(CategoriaProdutoModel categoriaProduto) {
		this.categoriaProduto = categoriaProduto;
	}
	
	public MarcaProdutoModel getMarcaProduto() {
		return marcaProduto;
	}

	public void setMarcaProduto(MarcaProdutoModel marcaProduto) {
		this.marcaProduto = marcaProduto;
	}
	
	public void setImagens(List<ImagemProdutoModel> imagens) {
		this.imagens = imagens;
	}
	
	public List<ImagemProdutoModel> getImagens() {
		return imagens;
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
		ProdutoModel other = (ProdutoModel) obj;
		return Objects.equals(id, other.id);
	}
}

