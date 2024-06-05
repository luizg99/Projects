package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import com.lojavirtual.LojaVirtualApplication;
import com.lojavirtual.controller.PessoaController;
import com.lojavirtual.model.PessoaJuridicaModel;

import junit.framework.TestCase;
import util.ExceptionMentoriaJava;

@Profile("test")
@SpringBootTest(classes = LojaVirtualApplication.class)
public class TestePessoaUsuario extends TestCase {
	
	@Autowired
	private PessoaController pessoaController;
	
	@Test
	public void testCadPessoaFisica() throws ExceptionMentoriaJava {
		
		PessoaJuridicaModel pessoaJuridica = new PessoaJuridicaModel();
				
		pessoaJuridica.setCnpj("70459580132");
		pessoaJuridica.setNome("Luiz");
		pessoaJuridica.setEmail("luiz@gmail.com");
		pessoaJuridica.setTelefone("62981265245");
		//pessoaJuridica.setInscricaoEstadual("12321332");
		//pessoaJuridica.setInscricaoMunicipal("123215423");
		//pessoaJuridica.setFantasia("ltda recursos");
		//pessoaJuridica.setRazaoSocial("ltda do luizn");
		pessoaJuridica.setCategoria("fodase");
		//pessoaJuridica.setEmpresa(null);
		
		
		pessoaController.salvarPj(null);
		
		
		
		//Integer vTeste = 0;
		
		
		
		//pessoaJuridica.setEmpresa(pessoaJuridica);		
		
		/*
		PessoasFisicas pessoaFisica = new PessoasFisicas()
				
		pessoaFisica.setCpf("70459580132");
		pessoaFisica.setNome("Luiz");
		pessoaFisica.setEmail("luiz@gmail.com");
		pessoaFisica.setTelefone("62981265245");
		pessoaFisica.setEmpresa(pessoaFisica);*/		
	}
}