
// Define o pacote onde a classe está localizada
package com.lojavirtual.security;

// Importa classes necessárias para manipulação de JWK (JSON Web Key)
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

// Importa anotações e classes do Spring Framework e Spring Security
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Importa classes para manipulação de chaves RSA
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration // Define que esta classe é uma classe de configuração do Spring
@EnableWebSecurity // Habilita a segurança da web no Spring Security
@EnableMethodSecurity // Habilita a segurança baseada em métodos (como @PreAuthorize)
public class ConfigSecurity {

    // Injeta a chave pública RSA a partir de propriedades configuradas
    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;

    // Injeta a chave privada RSA a partir de propriedades configuradas
    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    @Bean // Define um bean para a cadeia de filtros de segurança
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configura as regras de autorização e segurança HTTP
        http
            .authorizeHttpRequests(authorize -> authorize // Permite todas as requisições POST para o endpoint "/login"
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .anyRequest().authenticated()) // Requer autenticação para qualquer outra requisição
            .csrf(csrf -> csrf.disable()) // Desabilita a proteção CSRF (Cross-Site Request Forgery)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Configura o servidor de recursos OAuth2 para usar JWTs
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Configura a política de criação de sessões como "STATELESS"

        // Retorna a configuração de segurança construída
        return http.build();
    }

    // Define um bean para decodificação de JWTs
    @Bean
    public JwtDecoder jwtDecoder() {
        // Cria um decodificador de JWT usando a chave pública RSA
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() { // encoder para poder fazer a decodificação do scope
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(privateKey).build(); // instaciando um jwk com a construção da chave
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk)); // aque ele recebe e usa o immutablejwkset pra não deixar fazer alteracao em nenhum momento 
        
        return new NimbusJwtEncoder(jwks); // so retornando o objeto que foi criado
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(); //semente de criacao do encoder da senha
    }

}