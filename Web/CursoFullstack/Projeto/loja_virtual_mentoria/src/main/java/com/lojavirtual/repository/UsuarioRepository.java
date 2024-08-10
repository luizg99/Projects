package com.lojavirtual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lojavirtual.model.UsuarioModel;

import jakarta.transaction.Transactional;

@Repository
public interface UsuarioRepository extends CrudRepository<UsuarioModel, Long>{

	@Query(value = "select * from USUARIOS where LOGIN = ?1", nativeQuery = true)
	Optional<UsuarioModel> findUserByLogin(String login);
	
	@Query(value = "select * from USUARIOS where pessoa_id = ?1 or login = ?2", nativeQuery = true)
	UsuarioModel findUserByPessoa(Long id, String email);
	
	@Query(value = "select * from USUARIOS where DATA_ATUAL_SENHA <= current_date - 90", nativeQuery = true)
	List<UsuarioModel> usuarioSenhaVencida();

	@Query(
			value = "select constraint_name from information_schema.constraint_column_usage \r\n"
			+ "where table_name = 'usuarios_acessos' \r\n"
			+ "and column_name = 'acesso_id'\r\n"
			+ "and constraint_name <> 'uni_acesso_usuario';", nativeQuery = true
	)
	String consultaConstraintAcesso();

	@Transactional
	@Modifying
	@Query(value = "insert into USUARIOS_ACESSOS(usuario_id, acesso_id) values (?1, (select ID from ACESSOS where DESCRICAO = 'ROLE_USER'))", nativeQuery = true)
	void insereAcessoUsuarioPessoa(Long id);
	
	@Transactional
	@Modifying
	@Query(value = "insert into USUARIOS_ACESSOS(usuario_id, acesso_id) values (?1, (select ID from ACESSOS where DESCRICAO = ?2))", nativeQuery = true)
	void insereAcessoUsuarioPessoaJuridica(Long id, String acesso);
}
