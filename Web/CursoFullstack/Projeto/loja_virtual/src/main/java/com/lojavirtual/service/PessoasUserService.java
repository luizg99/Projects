package com.lojavirtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lojavirtual.repository.UsuarioRepository;

@Service
public class PessoasUserService {
	@Autowired
	private UsuarioRepository usuarioRepository;
}
