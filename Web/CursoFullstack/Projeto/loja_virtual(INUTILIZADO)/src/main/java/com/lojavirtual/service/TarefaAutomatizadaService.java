package com.lojavirtual.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lojavirtual.model.Usuarios;
import com.lojavirtual.repository.UsuarioRepository;

@Service
public class TarefaAutomatizadaService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceSendEmail sendEmail;
	
	
	@Scheduled(initialDelay = 2000, fixedDelay = 86400000) //exemplo para rodar teste
	//@Scheduled(cron = " 0 0 11 * * *", zone = "America/Sao_Paulo") // exemplo para rodar todo dia as 11hrs
	public void notificarUsuarioTrocaSenha() throws UnsupportedEncodingException, MessagingException, InterruptedException {
		
		List<Usuarios> usuarios = usuarioRepository.usuarioSenhaVencida();
		
		for (Usuarios usuario : usuarios) {
			StringBuilder msg = new StringBuilder();
			
			msg.append("Olá, ").append(usuario.getPessoa().getNome()).append("<br/>");
			msg.append("Está na hora de trocar sua senha, já se passaram 90 dias de validade.").append("<br/>");
			msg.append("Loja Virtual do Nunes - JDEV Treinamento");
			
			sendEmail.enviarEmailHtml("Troca de senha", msg.toString(), usuario.getLogin());
			
			Thread.sleep(3000);
		}
		
	}
}
