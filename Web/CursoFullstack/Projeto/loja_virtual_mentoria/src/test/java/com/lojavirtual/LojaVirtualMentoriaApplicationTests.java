package com.lojavirtual;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lojavirtual.controller.AcessoController;
import com.lojavirtual.model.AcessoModel;
import com.lojavirtual.repository.AcessoRepository;

import junit.framework.TestCase;

@Profile("test")
@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
public class LojaVirtualMentoriaApplicationTests extends TestCase {
	
//	@Autowired
//	private AcessoService acessoService;
	
	@Autowired
	private AcessoRepository acessoRepository;
	
	@Autowired
	private AcessoController acessoController;
	
	@Autowired
	private WebApplicationContext wac;
	
	@Test
	public void testRestApiCadastroAcesso() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		AcessoModel acesso = new AcessoModel();
		acesso.setDescricao("ROLE_COMPRADOR");
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.post("/salvarAcesso")
				.content(objectMapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		System.out.println("Retorno da api: " + retornoApi.andReturn().getResponse().getContentAsString());
		
		AcessoModel objetoRetorno = objectMapper.readValue(retornoApi.andReturn().getResponse().getContentAsString(), AcessoModel.class);
		
		assertEquals(acesso.getDescricao(), objetoRetorno.getDescricao());
	}
	
	@Test
	public void testRestApiDeleteAcesso() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		AcessoModel acesso = new AcessoModel();
		acesso.setDescricao("ROLE_TESTE_DELETE");
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.post("/deleteAcesso")
				.content(objectMapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		System.out.println("Retorno da api: " + retornoApi.andReturn().getResponse().getContentAsString());
		System.out.println("Status da api: " + retornoApi.andReturn().getResponse().getStatus());
		
		assertEquals("Acesso removido.", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
	}
	
	@Test
	public void testRestApiDeleteAcessoPorId() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		AcessoModel acesso = new AcessoModel();
		acesso.setDescricao("ROLE_TESTE_DELETE_ID");
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.delete("/deleteAcessoPorId/" + acesso.getId())
				.content(objectMapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		
		System.out.println("Retorno da api por id: " + retornoApi.andReturn().getResponse().getContentAsString());
		System.out.println("Status da api por id: " + retornoApi.andReturn().getResponse().getStatus());
		
		assertEquals("Acesso removido.", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
	}

	@Test
	public void testRestApiObterAcessoPorId() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		AcessoModel acesso = new AcessoModel();
		acesso.setDescricao("ROLE_OBTER_ID");
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.get("/obterAcessoPorId/" + acesso.getId())
				.content(objectMapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		

		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
		
		AcessoModel objetoRetorno = objectMapper.readValue(retornoApi.andReturn().getResponse().getContentAsString(), AcessoModel.class);
		
		assertEquals(acesso.getDescricao(), objetoRetorno.getDescricao());
		assertEquals(acesso.getId(), objetoRetorno.getId());
	}
	
	@Test
	public void testRestApiObterAcessoPorDescricao() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();
		
		AcessoModel acesso = new AcessoModel();
		acesso.setDescricao("ROLE_OBTER_ID_POR_DESC");
		acesso = acessoRepository.save(acesso);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.get("/buscarAcessoPorDesc/OBTER_ID_POR")
				.content(objectMapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON));
		

		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
		
		List<AcessoModel> retornoApiList = objectMapper.readValue(retornoApi.andReturn().getResponse().getContentAsString(), new TypeReference<List<AcessoModel>>() {});
		assertEquals(1, retornoApiList.size());
		assertEquals(acesso.getDescricao(), retornoApiList.get(0).getDescricao());
		
		acessoRepository.deleteById(acesso.getId());
		
		acessoRepository.flush();
	}

	@Test
	public void testeCadastraAcesso() {
		
		AcessoModel acesso = new AcessoModel();
		
		acesso.setDescricao("ROLE_ADMIN");
		
		assertEquals(true, acesso.getId() == null);
		
		acesso = acessoController.salvarAcesso(acesso).getBody();
		
		assertEquals(true, acesso.getId() > 0);
		
		assertEquals("ROLE_ADMIN", acesso.getDescricao());
		
		AcessoModel acesso2 = acessoRepository.findById(acesso.getId()).get();
		
		assertEquals(acesso.getId(), acesso2.getId());
		
		acessoRepository.deleteById(acesso2.getId());
		
		acessoRepository.flush();
		
//		AcessoModel acesso3 = acessoRepository.findById(acesso2.getId()).orElse(null);
		
//		assertEquals(null, acesso3 == null);
		
		acesso = new AcessoModel();
		acesso.setDescricao("ROLE_ALUNO");
		
		acesso = acessoController.salvarAcesso(acesso).getBody();
		
		List<AcessoModel> acessos = acessoRepository.buscarAcessoDescricao("ALUNO".trim().toUpperCase());
		
		assertEquals(true, acessos.size() > 1);
		
		acessoRepository.deleteById(acesso.getId());
	}
	
	
	

}