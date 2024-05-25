package com.lojavirtual.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.Usuarios;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuarios, Long> {

	@Query(value = "select * from Usuarios u where u.login = ?1", nativeQuery = true)
	Optional<Usuarios> findUserByLogin(String login);
}
