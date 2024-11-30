package com.lojavirtual;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.Entity;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EntityScan(basePackages = "com.lojavirtual.model")
@ComponentScan(basePackages = {"com.*"})
@EnableJpaRepositories(basePackages = {"com.lojavirtual.repository"})
@EnableTransactionManagement
public class LojaVirtualApplication implements AsyncConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(LojaVirtualApplication.class, args);
		
		System.out.println(new BCryptPasswordEncoder().encode("123456")); // ISSO DAQ É SÓ PRA FAZER OQ ELE FEZ NO VIDEO DE GERAR A CHAVE CRIPTOGRAFADA)
	}
	
	@Override
	@Bean
	public Executor getAsyncExecutor() {
		
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Assyncrono Thread");
		executor.initialize();
		
		return executor;
	}

}
