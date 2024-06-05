package com.lojavirtual.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
/*
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
*/
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EnvioEmailService {
/*
	private String userName = "4321beyblade@gmail.com";
	private String senha = "yprp xldh vgjc whcr";

	@Async
	public void enviarEmailHtml(String assunto, String mensagem, String emailDestino)
			throws UnsupportedEncodingException, MessagingException {
		Properties properties = new Properties();
		properties.put("mail.smtp.ssl.trust", "*");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls", "false");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(userName, senha);
			}

		});

		session.setDebug(true);

		Address[] toUser = InternetAddress.parse(emailDestino);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(userName, "Nunes - da Altis Sistemas", "UTF-8"));
		message.setRecipients(Message.RecipientType.TO, toUser);
		message.setSubject(assunto);
		message.setContent(mensagem, "text/html; charset=utf-8");

		Transport.send(message);
	}
*/
}
