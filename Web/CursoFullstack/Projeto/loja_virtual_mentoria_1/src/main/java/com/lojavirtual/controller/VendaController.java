package com.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.EnderecoModel;
import com.lojavirtual.model.PessoaFisicaModel;
import com.lojavirtual.model.StatusRastreioModel;
import com.lojavirtual.model.VendaItemModel;
import com.lojavirtual.model.VendaModel;
import com.lojavirtual.model.dto.VendaDTO;
import com.lojavirtual.model.dto.VendaItemDTO;
import com.lojavirtual.repository.EnderecoRepository;
import com.lojavirtual.repository.NotaFiscalVendaRepository;
import com.lojavirtual.repository.StatusRastreioRepository;
import com.lojavirtual.repository.VendaRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "**/venda")
public class VendaController {
	
    // A anotação @RestController indica que a classe é um controlador REST.
    // @RequestMapping define que todas as requisições que começam com o caminho especificado serão direcionadas para este controlador.

    @Autowired
    private VendaRepository vendaRepository;
    // Injeta uma instância do repositório de vendas para que ele possa ser usado nos métodos do controlador.

    @Autowired
    private PessoaController pessoaController;
    // Injeta o controlador de pessoas para manipular operações relacionadas a pessoas físicas dentro da venda.

    @Autowired
    private EnderecoRepository enderecoRepository;
    // Injeta o repositório de endereços para salvar e recuperar dados de endereço.

    @Autowired
    private NotaFiscalVendaRepository notaFiscalVendaRepository;
    // Injeta o repositório de notas fiscais de venda.

    @Autowired
    private StatusRastreioRepository statusRastreioRepository;
    // Injeta o repositório de status de rastreio.

    @PostMapping  
    public ResponseEntity<VendaDTO> salvarVenda(@RequestBody @Valid VendaModel venda) throws ExceptionMentoriaJava {
		// Este método mapeia requisições HTTP POST para o caminho "/venda".
        // Ele recebe um objeto VendaModel no corpo da requisição, valida-o, e pode lançar uma exceção personalizada.

        if (venda.getPessoa().getId() <= 0) {
            venda.getPessoa().setEmpresa(venda.getEmpresa());
            PessoaFisicaModel cliente = pessoaController.salvarPessoaFisica(venda.getPessoa()).getBody();
            venda.setPessoa(cliente);
            // Verifica se a pessoa associada à venda é nova (id menor ou igual a 0).
            // Se for, associa a empresa à pessoa e salva a pessoa chamando o método salvarPessoaFisica do PessoaController.
            // O resultado é definido como a pessoa da venda.
        }

        if (venda.getEnderecoCobranca().getId() <= 0) {
            EnderecoModel enderecoCobranca = venda.getEnderecoCobranca();
            enderecoCobranca = enderecoRepository.save(enderecoCobranca);
            venda.setEnderecoCobranca(enderecoCobranca);
            // Verifica se o endereço de cobrança é novo.
            // Se for, salva o endereço no repositório e atualiza a venda com o endereço salvo.
        }

        if (venda.getEnderecoEntrega().getId() <= 0) {
            EnderecoModel enderecoEntrega = venda.getEnderecoEntrega();
            enderecoEntrega = enderecoRepository.save(enderecoEntrega);
            venda.setEnderecoEntrega(enderecoEntrega);
            // Verifica se o endereço de entrega é novo e faz o mesmo procedimento de salvar e associar à venda.
        }

        venda.getNotaFiscalVenda().setEmpresa(venda.getEmpresa());
        // Associa a empresa à nota fiscal da venda.

        for (int i = 0; i < venda.getItens().size(); i++) {
            venda.getItens().get(i).setEmpresa(venda.getEmpresa());
            venda.getItens().get(i).setVenda(venda);
            // Itera sobre os itens da venda, associando a empresa e a venda a cada item.
        }

        venda = vendaRepository.saveAndFlush(venda);
        // Salva a venda no banco de dados e garante que as mudanças sejam imediatamente aplicadas.

        StatusRastreioModel statusRastreio = new StatusRastreioModel();
        statusRastreio.setCentroDistribuicao("CD");
        statusRastreio.setCidade("CD");
        statusRastreio.setEmpresa(venda.getEmpresa());
        statusRastreio.setEstado("GO");
        statusRastreio.setVenda(venda);
        statusRastreioRepository.save(statusRastreio);
        // Cria e salva um novo status de rastreio para a venda, associando-o à empresa e à venda.

        venda.getNotaFiscalVenda().setVenda(venda);
        notaFiscalVendaRepository.saveAndFlush(venda.getNotaFiscalVenda());
        // Associa a venda à nota fiscal e salva a nota fiscal no banco de dados.

        VendaDTO vendaDto = new VendaDTO();
        vendaDto.setId(venda.getId());
        vendaDto.setValorTotal(venda.getValorTotal());
        vendaDto.setPesssoa(venda.getPessoa());
        vendaDto.setEnderecoCobranca(venda.getEnderecoCobranca());
        vendaDto.setEnderecoEntrega(venda.getEnderecoEntrega());
        vendaDto.setValorDesconto(venda.getValorDesconto());
        vendaDto.setValorFrete(venda.getValorFrete());
        // Cria um DTO (Data Transfer Object) da venda para ser retornado na resposta.
        // O DTO é uma forma de representar a venda sem expor o modelo diretamente.

        for (VendaItemModel item : venda.getItens()) {
            VendaItemDTO itemDto = new VendaItemDTO();
            itemDto.setProduto(item.getProduto());
            itemDto.setQuantidade(item.getQuantidade());
            vendaDto.getItens().add(itemDto);
            // Itera sobre os itens da venda e os converte em DTOs para incluir no DTO da venda.
        }

        return new ResponseEntity<VendaDTO>(vendaDto, HttpStatus.OK);
        // Retorna o DTO da venda encapsulado em um ResponseEntity com status HTTP 200 (OK).
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<VendaDTO> buscar(@PathVariable Long id) {
        // Este método mapeia requisições HTTP GET para "/venda/{id}".
        VendaModel venda = vendaRepository.findById(id).orElse(null);
        // Busca uma venda pelo ID, se não encontrar retorna null.
        
        VendaDTO vendaDto = new VendaDTO();
        vendaDto.setId(venda.getId());
        vendaDto.setValorTotal(venda.getValorTotal());
        vendaDto.setPesssoa(venda.getPessoa());
        vendaDto.setEnderecoCobranca(venda.getEnderecoCobranca());
        vendaDto.setEnderecoEntrega(venda.getEnderecoEntrega());
        vendaDto.setValorDesconto(venda.getValorDesconto());
        vendaDto.setValorFrete(venda.getValorFrete());
        // Cria um DTO da venda para ser retornado na resposta.

        for (VendaItemModel item : venda.getItens()) {
            VendaItemDTO itemDto = new VendaItemDTO();
            itemDto.setProduto(item.getProduto());
            itemDto.setQuantidade(item.getQuantidade());
            vendaDto.getItens().add(itemDto);
            // Itera sobre os itens da venda e os converte em DTOs para incluir no DTO da venda.
        }

        return new ResponseEntity<VendaDTO>(vendaDto, HttpStatus.OK);
        // Retorna o DTO da venda encapsulado em um ResponseEntity com status HTTP 200 (OK).
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        // Este método mapeia requisições HTTP DELETE para "/venda/{id}".
        vendaRepository.deletarVenda(id);
        // Deleta a venda pelo ID.

        return new ResponseEntity<String>("Venda excluída com sucesso.", HttpStatus.OK);
        // Retorna uma mensagem de sucesso com status HTTP 200 (OK).
    }
}
