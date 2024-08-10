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

import com.lojavirtual.model.AcessoModel;
import com.lojavirtual.model.dto.LoginRequest;
import com.lojavirtual.model.dto.LoginResponse;
import com.lojavirtual.repository.UsuarioRepository;

@RestController
public class TokenController {
	
	private final JwtEncoder jwtEncoder;
    private final UsuarioRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public TokenController(
    		JwtEncoder jwtEncoder,
    		UsuarioRepository userRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login") 
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) { 

        var usuario = userRepository.findUserByLogin(loginRequest.login()); 

        if (usuario.isEmpty() || !usuario.get().isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Usuario ou senha não estão corretos!");  
        } 

        var now = Instant.now(); 
        var expiresIn = 3600L; 

        var scopes = usuario.get().getAcessos() 
                .stream()
                .map(AcessoModel::getDescricao) 
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("altis")
                .subject(usuario.get().getUsername().toString())  
                .issuedAt(now) 
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
    }
}
