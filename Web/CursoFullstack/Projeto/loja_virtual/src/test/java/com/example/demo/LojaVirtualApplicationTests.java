package com.example.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.lojavirtual.LojaVirtualApplication;
import com.lojavirtual.controller.AcessoController;
import com.lojavirtual.model.Acessos;
import com.lojavirtual.repository.AcessosRepository;

import junit.framework.TestCase;

@SpringBootTest(classes = LojaVirtualApplication.class)
public class LojaVirtualApplicationTests extends TestCase {
	/*
	@Autowired
	private AcessosService acessosService;
	
	@Autowired
	private AcessosRepository acessosRepository; 
	*/
	@Autowired
	private AcessoController acessoController;

	@Autowired
	private AcessosRepository acessoRepository;
	
	@Test
	public void testeCadastrarAcesso() {
		Acessos acesso = new Acessos();
		
		acesso.setDescricao("ROLE_ADMIN");

		assertEquals(true, acesso.getId() == null);
		
		acesso = acessoController.salvarAcesso(acesso).getBody();
		
		assertEquals(true, acesso.getId() > 0);
		
		assertEquals("ROLE_ADMIN", acesso.getDescricao());
		
		/*Teste de carregamento*/
		Acessos acesso2 = acessoRepository.findById(acesso.getId()).get();
		
		assertEquals(acesso.getId(), acesso2.getId());

		/*teste de delete*/
		acessoRepository.deleteById(acesso2.getId());
		
		acessoRepository.flush();
		
//		Acessos acesso3 = acessoRepository.findById(acesso2.getId()).orElse(null);
		
//		assertEquals(null, acesso3 == null);
		
		acesso = new Acessos();
		acesso.setDescricao("ROLE_ALUNO");
		
		acesso = acessoController.salvarAcesso(acesso).getBody();
		
		List<Acessos> acessos = acessoRepository.buscarAcessoDescricao("ALUNO".trim().toUpperCase());
		
		assertEquals(true, acessos.size() > 0);
		
		acessoRepository.deleteById(acesso.getId());
	}
	
	
	

}