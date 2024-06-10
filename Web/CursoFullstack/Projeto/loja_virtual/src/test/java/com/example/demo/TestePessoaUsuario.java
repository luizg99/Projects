package com.example.demo;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import com.lojavirtual.LojaVirtualApplication;
import com.lojavirtual.controller.PessoaController;
import com.lojavirtual.enums.TipoEndereco;
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
		pessoaJuridica.setCnpj("" + Calendar.getInstance().getTimeInMillis());
		pessoaJuridica.setNome("Lucas Gabriel");
		pessoaJuridica.setEmail("lucasaltisnADSDASDuASDASDnAAes@gmail.com");
		pessoaJuridica.setTelefone("62970707070");
		pessoaJuridica.setInscricao_municipal("54654654");
		pessoaJuridica.setInscricao_municipal("5465456465");
		pessoaJuridica.setFantasia("Luquinhas");
		pessoaJuridica.setRazao_social("Lucas LTDA");

		Enderecos endereco1 = new Enderecos();
		endereco1.setBairro("bairro");
		endereco1.setCep("564564");
		endereco1.setCidade("cidade");
		endereco1.setComplemento("adasdasds");
		endereco1.setEmpresa(pessoaJuridica);
		endereco1.setLogradouro("logradouro");
		endereco1.setNumero("213321");
		endereco1.setPessoa(pessoaJuridica);
		endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
		endereco1.setUf("uf1");

		Enderecos endereco2 = new Enderecos();
		endereco2.setBairro("bairro");
		endereco2.setCep("564564");
		endereco2.setCidade("cidade");
		endereco2.setComplemento("adasdasds");
		endereco2.setEmpresa(pessoaJuridica);
		endereco2.setLogradouro("logradouro");
		endereco2.setNumero("213321");
		endereco2.setPessoa(pessoaJuridica);
		endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
		endereco2.setUf("uf1");

		pessoaJuridica.getEnderecos().add(endereco1);
		pessoaJuridica.getEnderecos().add(endereco2);

		pessoaJuridica = pessoaController.salvarPj(pessoaJuridica).getBody();

		assertEquals(true, pessoaJuridica.getId() > 0);

		for (Enderecos endereco : pessoaJuridica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}

		assertEquals(2, pessoaJuridica.getEnderecos().size());
	}
	
	@Test
	public void testCadPessoaFisica() throws ExceptionMentoriaJava {
		
		PessoaJuridicaModel pessoaJuridica = pessoaJuridicaRepository.existeCnpjCadastrado("49.884.753/0001-04");

		PessoaFisicaModel pessoaFisica = new PessoaFisicaModel();
		pessoaFisica.setCpf("06221179122");
		pessoaFisica.setNome("Lucas Nunes");
		pessoaFisica.setEmail("lucasssssss@gmail.com");
		pessoaFisica.setTelefone("62970707070");
		pessoaFisica.setEmpresa(pessoaJuridica);
		
		EnderecoModel endereco1 = new EnderecoModel();
		endereco1.setBairro("bairro");
		endereco1.setCep("74490255");
		endereco1.setCidade("cidade");
		endereco1.setComplemento("adasdasds");
		endereco1.setEmpresa(pessoaJuridica);
		endereco1.setLogradouro("logradouro");
		endereco1.setNumero("213321");
		endereco1.setPessoa(pessoaFisica);
		endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
		endereco1.setUf("uf1");

		EnderecoModel endereco2 = new EnderecoModel();
		endereco2.setBairro("bairro");
		endereco2.setCep("74490255");
		endereco2.setCidade("cidade");
		endereco2.setComplemento("adasdasds");
		endereco2.setEmpresa(pessoaJuridica);
		endereco2.setLogradouro("logradouro");
		endereco2.setNumero("213321");
		endereco2.setPessoa(pessoaFisica);
		endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
		endereco2.setUf("uf1");

		pessoaFisica.getEnderecos().add(endereco1);
		pessoaFisica.getEnderecos().add(endereco2);

		pessoaFisica = pessoaController.salvarPessoaFisica(pessoaFisica).getBody();

		assertEquals(true, pessoaFisica.getId() > 0);

		for (EnderecoModel endereco : pessoaFisica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}

		assertEquals(2, pessoaFisica.getEnderecos().size());
	}
}