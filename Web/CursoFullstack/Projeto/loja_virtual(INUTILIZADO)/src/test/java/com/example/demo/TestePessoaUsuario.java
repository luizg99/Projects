package com.example.demo;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import com.lojavirtual.LojaVirtualApplication;
import com.lojavirtual.controller.PessoaController;
import com.lojavirtual.enums.TipoEndereco;
import com.lojavirtual.enums.TipoPessoa;
import com.lojavirtual.model.Enderecos;
import com.lojavirtual.model.PessoaFisicaModel;
import com.lojavirtual.model.PessoaJuridicaModel;
import com.lojavirtual.repository.PessoaJuridicaRepository;

import junit.framework.TestCase;
import util.ExceptionMentoriaJava;

@Profile("test")
@SpringBootTest(classes = LojaVirtualApplication.class)
public class TestePessoaUsuario extends TestCase {
	
	@Autowired
	private PessoaController pessoaController;
	
	@Autowired
	private PessoaJuridicaRepository pessoaJuridicaRepository;
	
	
	@Test
	public void testCadPessoaJuridica() throws ExceptionMentoriaJava {

		PessoaJuridicaModel pessoaJuridica = new PessoaJuridicaModel();
		
		pessoaJuridica.setCnpj("88.372.202/0001-05");
		pessoaJuridica.setInscricao_municipal("141785575");
		pessoaJuridica.setInscricao_estadual("4171717");
		pessoaJuridica.setFantasia("Luiz1");
		pessoaJuridica.setRazao_social("Empresa LTDA");
		pessoaJuridica.setCategoria("teste");
		pessoaJuridica.setEmpresa(pessoaJuridica);
		pessoaJuridica.setEmail("klotz.oficial@gmail.com");
		pessoaJuridica.setNome("Luiz");
		pessoaJuridica.setTelefone("62970707070");
		pessoaJuridica.setTipo_pessoa(TipoPessoa.JURIDICA.name());
		
		Enderecos endereco1 = new Enderecos();
		endereco1.setBairro("bairro");
		endereco1.setCep("74923090");
		endereco1.setCidade("cidade");
		endereco1.setComplemento("aaaaaaaaaaaaa");
		endereco1.setEmpresa(pessoaJuridica);
		endereco1.setLogradouro("logradouro");
		endereco1.setNumero("01");
		endereco1.setRua("rua 1");
		endereco1.setPessoa(pessoaJuridica);
		endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
		endereco1.setUf("uf1");

		Enderecos endereco2 = new Enderecos();
		endereco2.setBairro("bairro");
		endereco2.setCep("74845060");
		endereco2.setCidade("cidade");
		endereco2.setComplemento("afafafafaf");
		endereco2.setEmpresa(pessoaJuridica);
		endereco2.setLogradouro("logradouro");
		endereco2.setNumero("213321");
		endereco2.setRua("rua 2");
		endereco2.setPessoa(pessoaJuridica);
		endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
		endereco2.setUf("uf1");

		pessoaJuridica.getEnderecos().add(endereco1);
		pessoaJuridica.getEnderecos().add(endereco2);

		pessoaJuridica = pessoaController.salvarPessoaJuridica(pessoaJuridica).getBody();

		assertEquals(true, pessoaJuridica.getId() > 0);

		for (Enderecos endereco : pessoaJuridica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}

		assertEquals(2, pessoaJuridica.getEnderecos().size());
	}
	/*
	
	@Test
	public void testCadPessoaFisica() throws ExceptionMentoriaJava {
		
		PessoaJuridicaModel pessoaJuridica = pessoaJuridicaRepository.existeCnpjCadastrado("589559598");

		PessoaFisicaModel pessoaFisica = new PessoaFisicaModel();
		pessoaFisica.setCpf("05761316366");
		pessoaFisica.setNome("Luiz Lindo");
		pessoaFisica.setEmail("asdasdasdgasd@gmail.com");
		pessoaFisica.setTelefone("62547525456546");
		pessoaFisica.setEmpresa(pessoaJuridica);
		
		Enderecos endereco1 = new Enderecos();
		endereco1.setBairro("bairro");
		endereco1.setCep("74490255");
		endereco1.setCidade("cidade");
		endereco1.setComplemento("adasdasds");
		endereco1.setEmpresa(pessoaJuridica);
		endereco1.setLogradouro("logradouro");
		endereco1.setNumero("213321");
		endereco1.setRua("afasf");
		endereco1.setPessoa(pessoaFisica);
		endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
		endereco1.setUf("uf1");

		Enderecos endereco2 = new Enderecos();
		endereco2.setBairro("bairro");
		endereco2.setCep("74490255");
		endereco2.setCidade("cidade");
		endereco2.setComplemento("adasdasds");
		endereco2.setEmpresa(pessoaJuridica);
		endereco2.setLogradouro("logradouro");
		endereco2.setNumero("213321");
		endereco2.setRua("afasf");
		endereco2.setPessoa(pessoaFisica);
		endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
		endereco2.setUf("uf1");

		pessoaFisica.getEnderecos().add(endereco1);
		pessoaFisica.getEnderecos().add(endereco2);

		pessoaFisica = pessoaController.salvarPessoaFisica(pessoaFisica).getBody();

		assertEquals(true, pessoaFisica.getId() > 0);

		for (Enderecos endereco : pessoaFisica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}

		assertEquals(2, pessoaFisica.getEnderecos().size());
		
	}*/
	
}