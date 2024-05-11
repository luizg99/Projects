package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.lojavirtual.LojaVirtualApplication;
import com.lojavirtual.controller.AcessoController;
import com.lojavirtual.model.Acessos;
import com.lojavirtual.repository.AcessosRepository;
import com.lojavirtual.service.AcessosService;

@SpringBootTest(classes = LojaVirtualApplication.class)
class LojaVirtualApplicationTests {
	
	@Autowired
	private AcessoController acessoController;
	
	@Test
	public void testeCadastrarAcesso() {
		Acessos acessos = new Acessos();
		
		acessos.setDescricao("ROLE_ADMIN");
		
		acessoController.salvarAcesso(acessos);

	}
	

}
