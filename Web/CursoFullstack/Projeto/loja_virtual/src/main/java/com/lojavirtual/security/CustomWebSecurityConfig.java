
package com.lojavirtual.security;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfig {

      @Bean
      public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfiguration()))
            .csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorizeRequests->
                authorizeRequests.requestMatchers("/*").permitAll())
            .httpBasic(Customizer.withDefaults());
        return http.build();
      }


    @Bean
    public CorsConfigurationSource corsConfiguration() {
      return request -> {
        org.springframework.web.cors.CorsConfiguration config = 
            new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedHeaders(Collections.singletonList(""));
        config.setAllowedMethods(Collections.singletonList(""));
        config.setAllowedOriginPatterns(Collections.singletonList(""));
        config.setAllowCredentials(true);
        return config;
      };
    }

}