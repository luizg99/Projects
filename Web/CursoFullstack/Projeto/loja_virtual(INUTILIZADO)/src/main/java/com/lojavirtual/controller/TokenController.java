package com.lojavirtual.controller;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.controller.dto.LoginRequest;
import com.lojavirtual.controller.dto.LoginResponse;
import com.lojavirtual.model.Acessos;
import com.lojavirtual.repository.UsuarioRepository;

@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public TokenController(JwtEncoder jwtEncoder,
    		UsuarioRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    } //injeção de dependencia

    @PostMapping("/login") // indicando o caminho da validação (/login)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) { //

        var login = userRepository.findUserByLogin(loginRequest.login()); // passando os parametro do dto de request login (basicamente o login e senha)

        if (login.isEmpty() || !login.get().isLoginCorrect(loginRequest, passwordEncoder)) { // expection pra validar se o login ta correto ou não
            throw new BadCredentialsException("usuario ou senha não estão corretos!");
        }

        var now = Instant.now(); // metodo instant.now pra pegar o momento de agora pra saber quando o token foi criado
        var expiresIn = 3600L; // Tempo até o token expirar

        var scopes = login.get().getAcessos() //pegando os acessos para jogar no escopo
                .stream()
                .map(Acessos::getDescricao) // pega a descrição do acesso (para guardar por exemplo ACESSO_ADMIN)
                .collect(Collectors.joining(" "));// Collector para juntar se tiver mais de um Acesso definidio para ele

        var claims = JwtClaimsSet.builder() //setando as definiçoes para construir o token
                .issuer("altis") // Quem realizou a assinatura (personalizavel)
                .subject(login.get().getUsername().toString()) //pegando o 
                .issuedAt(now) //momento da assinatura
                .expiresAt(now.plusSeconds(expiresIn))//momento que acaba a validade do token
                .claim("scope", scopes)// usando a var scopes
                .build();//construindo o token

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(); // armazenando o token encodado na variavel

        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn)); // retornando para o dto O jwtValue e o ExpiresIn (dto recebe jwtValue como acessToken)
    }
}