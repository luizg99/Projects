package com.lojavirtual.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lojavirtual.controller.dto.CepDTO;
import com.lojavirtual.controller.dto.ConsultaCnpjDto;
import com.lojavirtual.model.PessoaFisicaModel;
import com.lojavirtual.model.PessoaJuridicaModel;
import com.lojavirtual.model.Usuarios;
import com.lojavirtual.repository.PessoaFisicaRepository;
import com.lojavirtual.repository.PessoaJuridicaRepository;
import com.lojavirtual.repository.UsuarioRepository;

@Service
public class PessoaUsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PessoaJuridicaRepository pessoaRepository;
	
	@Autowired
	private PessoaFisicaRepository pessoaFisicaRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ServiceSendEmail sendEmail;

	public PessoaJuridicaModel salvarPessoaJuridica(PessoaJuridicaModel pessoaJuridica) {

		for (int i = 0; i < pessoaJuridica.getEnderecos().size(); i++) {
			pessoaJuridica.getEnderecos().get(i).setPessoa(pessoaJuridica);
			pessoaJuridica.getEnderecos().get(i).setEmpresa(pessoaJuridica);
		}

		pessoaJuridica = pessoaRepository.save(pessoaJuridica);

		Usuarios usuarioPj = usuarioRepository.findUserByPessoa(pessoaJuridica.getId(), pessoaJuridica.getEmail());

		if (usuarioPj == null) {
			String constraint = usuarioRepository.consultaConstraintAcesso();
			if (constraint != null) {
				jdbcTemplate.execute("begin; alter table usuarios_acessos drop constraint " + constraint + "; commit;");
			}

			usuarioPj = new Usuarios();
			usuarioPj.setDataAtualSenha(Calendar.getInstance().getTime());
			usuarioPj.setEmpresa(pessoaJuridica);
			usuarioPj.setPessoa(pessoaJuridica);
			usuarioPj.setLogin(pessoaJuridica.getEmail());

			String senha = "teste" + Calendar.getInstance().getTimeInMillis();
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senha);

			usuarioPj.setSenha(senhaCriptografada);

			usuarioPj = usuarioRepository.save(usuarioPj);

			usuarioRepository.insereAcessoUsuarioPessoa(usuarioPj.getId());
			usuarioRepository.insereAcessoUsuarioPessoaJuridica(usuarioPj.getId(), "ROLE_ADMIN");

			StringBuilder mensagemHtml = new StringBuilder();
			mensagemHtml.append("<b>Segue abaixo seus dados de acesso para a loja virtual</b><br>");
			mensagemHtml.append("<b>Login: </b>" + pessoaJuridica.getEmail() + "<br>");
			mensagemHtml.append("<b>Senha: </b>" + senha + "<br>");
			mensagemHtml.append("<b>Obrigado!</b>");

			try {
				sendEmail.enviarEmailHtml("Acesso gerado para Loja Virtual", mensagemHtml.toString(),
						pessoaJuridica.getEmail());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return pessoaJuridica;
	}

	public PessoaFisicaModel salvarPessoaFisica(PessoaFisicaModel pessoaFisica) {
		
		for (int i = 0; i < pessoaFisica.getEnderecos().size(); i++) {
			pessoaFisica.getEnderecos().get(i).setPessoa(pessoaFisica);
			pessoaFisica.getEnderecos().get(i).setEmpresa(pessoaFisica.getEmpresa());
		}

		pessoaFisica = pessoaFisicaRepository.save(pessoaFisica);

		Usuarios usuarioPf = usuarioRepository.findUserByPessoa(pessoaFisica.getId(), pessoaFisica.getEmail());

		if (usuarioPf == null) {
			String constraint = usuarioRepository.consultaConstraintAcesso();
			if (constraint != null) {
				jdbcTemplate.execute("begin; alter table usuarios_acessos drop constraint " + constraint + "; commit;");
			}

			usuarioPf = new Usuarios();
			usuarioPf.setDataAtualSenha(Calendar.getInstance().getTime());
			usuarioPf.setEmpresa(pessoaFisica.getEmpresa());
			usuarioPf.setPessoa(pessoaFisica);
			usuarioPf.setLogin(pessoaFisica.getEmail());

			String senha = "" + Calendar.getInstance().getTimeInMillis();
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senha);

			usuarioPf.setSenha(senhaCriptografada);

			usuarioPf = usuarioRepository.save(usuarioPf);

			//usuarioRepository.insereAcessoUsuarioPessoa(usuarioPf.getId());

			StringBuilder mensagemHtml = new StringBuilder();
			mensagemHtml.append("<b>Segue abaixo seus dados de acesso para a loja virtual</b><br>");
			mensagemHtml.append("<b>Login: </b>" + pessoaFisica.getEmail() + "<br>");
			mensagemHtml.append("<b>Senha: </b>" + senha + "<br>");
			mensagemHtml.append("<b>Obrigado!</b>");

			//try {
			//	envioEmailService.enviarEmailHtml("Acesso gerado para Loja Virtual", mensagemHtml.toString(), pessoaFisica.getEmail());
			//} catch (Exception e) {
			//	e.printStackTrace();
			//}
		}
		return pessoaFisica;
	}
	
	public CepDTO consultaCep(String cep) {
		return new RestTemplate().getForEntity("https://viacep.com.br/ws/" + cep + "/json/", CepDTO.class).getBody();
	}
	
	public ConsultaCnpjDto consultaCnpj(String cnpj) {
		return new RestTemplate().getForEntity("https://receitaws.com.br/v1/cnpj/" + cnpj, ConsultaCnpjDto.class).getBody();
	}

}