package com.lojavirtual.model.dto;

import com.lojavirtual.model.ProdutoModel;

public class VendaItemDTO {

    private Double quantidade;

    private ProdutoModel produto;

    public void setProduto(ProdutoModel produto) {
        this.produto = produto;
    }

    public ProdutoModel getProduto() {
        return produto;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getQuantidade() {
        return quantidade;
    }
}
