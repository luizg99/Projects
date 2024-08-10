package com.lojavirtual.security;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;

/*Criar a autenticação e retornar também a autenticação JWT*/
@Service
@Component
public class JWTTokenAutenticacaoService {

	/*Token de validade de 30 dias*/
	private static final long EXPIRATION_TIME = 959990000;
	
	/*Chave de senha para juntar com o JWT*/
	private static final String SECRET = "SADHJF";
	
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	/*Gera o tokent e da a resposta para o cliente com o JWT*/
	public void addAuthentication(HttpServletResponse response, String username) throws Exception {
		
		/*Montagem do Token*/
		String JWT = Jwts.builder()./*Chama o gerador de token*/
				setSubject(username). /*Adiciona o user*/
				setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))./*Tempo de expiração*/
				signWith(SignatureAlgorithm.HS512, SECRET).compact();
		
		/*Exe: bearer sadfasdfasdfadsfa*/
		String token = TOKEN_PREFIX + " " + JWT;
		
		/*Dá a resposta para a tela e para o cliente, pode ser OUTRA API, NAVEGADOR, APLICATIVO, JAVA SCRIPT, OUTRA CHAMADA JAVA*/
		response.addHeader(HEADER_STRING, token);
		
		liberacaoCores(response);
		
		/*Usado para ver no Postma para teste*/
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
		
		
	}
	
	/*Fazendo liberação contra o erro de COrs no navegador*/
	private void liberacaoCores(HttpServletResponse response) {
		
		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if (response.getHeader("Acess-Control-Request-Headers") == null) {
			response.addHeader("Acess-Control-Request-Headers", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
	
			
		
}