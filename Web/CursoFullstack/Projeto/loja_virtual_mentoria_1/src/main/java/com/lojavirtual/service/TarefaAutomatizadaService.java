package com.lojavirtual.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lojavirtual.model.UsuarioModel;
import com.lojavirtual.repository.UsuarioRepository;

@Service
public class TarefaAutomatizadaService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private EnvioEmailService envioEmailService;
	
	
//	@Scheduled(initialDelay = 2000, fixedDelay = 86400000)
	@Scheduled(cron = " 0 0 11 * * *", zone = "America/Sao_Paulo")
	public void notificarUsuarioTrocaSenha() throws UnsupportedEncodingException, MessagingException, InterruptedException {
		
		List<UsuarioModel> usuarios = usuarioRepository.usuarioSenhaVencida();
		
		for (UsuarioModel usuario : usuarios) {
			StringBuilder msg = new StringBuilder();
			
			msg.append("Olá, ").append(usuario.getPessoa().getNome()).append("<br/>");
			msg.append("Está na hora de trocar sua senha, já se passaram 90 dias de validade.").append("<br/>");
			msg.append("Loja Virtual do Nunes - JDEV Treinamento");
			
			envioEmailService.enviarEmailHtml("Troca de senha", msg.toString(), usuario.getLogin());
			
			Thread.sleep(3000);
		}
		
	}

}
