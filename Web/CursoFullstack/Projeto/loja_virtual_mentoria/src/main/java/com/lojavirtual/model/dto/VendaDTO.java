package com.lojavirtual.model.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.lojavirtual.model.EnderecoModel;
import com.lojavirtual.model.PessoaModel;

public class VendaDTO {

    private Long id;
    
    private BigDecimal valorTotal;

    private BigDecimal valorDesconto;

    private BigDecimal valorFrete;

    private PessoaModel pesssoa;

    private EnderecoModel enderecoCobranca;

    private EnderecoModel enderecoEntrega;

    private List<VendaItemDTO> itens = new ArrayList<VendaItemDTO>();

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setItens(List<VendaItemDTO> itens) {
        this.itens = itens;
    }

    public List<VendaItemDTO> getItens() {
        return itens;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setPesssoa(PessoaModel pesssoa) {
        this.pesssoa = pesssoa;
    }

    public PessoaModel getPesssoa() {
        return pesssoa;
    }

    public void setEnderecoCobranca(EnderecoModel enderecoCobranca) {
        this.enderecoCobranca = enderecoCobranca;
    }

    public EnderecoModel getEnderecoCobranca() {
        return enderecoCobranca;
    }

    public void setEnderecoEntrega(EnderecoModel enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public EnderecoModel getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public BigDecimal getValorFrete() {
        return valorFrete;
    }
}
