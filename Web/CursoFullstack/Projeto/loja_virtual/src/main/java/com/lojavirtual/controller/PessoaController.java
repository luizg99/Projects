package com.lojavirtual.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.controller.dto.CepDTO;
import com.lojavirtual.enums.TipoPessoa;
import com.lojavirtual.model.Acessos;
import com.lojavirtual.model.Enderecos;
import com.lojavirtual.model.PessoaFisicaModel;
import com.lojavirtual.model.PessoaJuridicaModel;
import com.lojavirtual.repository.EnderecoRepository;
import com.lojavirtual.repository.PessoaFisicaRepository;
import com.lojavirtual.repository.PessoaJuridicaRepository;
import com.lojavirtual.service.PessoaUsuarioService;

import jakarta.validation.Valid;
import util.ExceptionMentoriaJava;
import util.ValidaCNPJ;
import util.ValidaCPF;

@RestController
public class PessoaController {
	@Autowired
	private PessoaJuridicaRepository pessoaJuridicaRepository;
	
	@Autowired
	private PessoaFisicaRepository pessoaFisicaRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired 
	private PessoaUsuarioService pessoaUsuarioService;
    
	
	@GetMapping(value = "/consultaCep/{cep}")
	public ResponseEntity<CepDTO> consultaCep(@PathVariable("cep") String cep) {
		return new ResponseEntity<CepDTO>(pessoaUsuarioService.consultaCep(cep), HttpStatus.OK); 
	}
	
	/*
	@GetMapping(value = "/consultaCnpjReceita/{cnpj}")
	public ResponseEntity<ConsultaCnpjDTO> consultaCnpjReceita(@PathVariable("cnpj") String cnpj) {
		return new ResponseEntity<ConsultaCnpjDTO>(pessoaUsuarioService.consultCnpj(cnpj), HttpStatus.OK); 
	}
	*/
	@GetMapping(value = "/consultaPessoaFisicaNome/{nome}")
	public ResponseEntity<List<PessoaFisicaModel>> consultaPessoaFisicaNome(@PathVariable("nome") String nome) {
		return new ResponseEntity<List<PessoaFisicaModel>>(pessoaFisicaRepository.pesquisaPorNome(nome.trim().toUpperCase()), HttpStatus.OK); 
	}
	
	@GetMapping(value = "/consultaPessoaFisicaCpf/{cpf}")
	public ResponseEntity<List<PessoaFisicaModel>> consultaPessoaFisicaCpf(@PathVariable("cpf") String cpf) {
		return new ResponseEntity<List<PessoaFisicaModel>>(pessoaFisicaRepository.pesquisaPorCpf(cpf), HttpStatus.OK); 
	}
	
	@GetMapping(value = "/consultaPessoaJuridicaNome/{nome}")
	public ResponseEntity<List<PessoaJuridicaModel>> consultaPessoaJuridicaNome(@PathVariable("nome") String nome) {
		return new ResponseEntity<List<PessoaJuridicaModel>>(pessoaJuridicaRepository.pesquisaPorNome(nome.trim().toUpperCase()), HttpStatus.OK); 
	}
	
	@GetMapping(value = "/consultaPessoaJuridicaCnpj/{cnpj}")
	public ResponseEntity<List<PessoaJuridicaModel>> consultaPessoaJuridicaCnpj(@PathVariable("cnpj") String cnpj) {
		return new ResponseEntity<List<PessoaJuridicaModel>>(pessoaJuridicaRepository.pesquisaPorCnpj(cnpj), HttpStatus.OK); 
	}

	@PostMapping(value = "/salvarPessoaJuridica")
	public ResponseEntity<PessoaJuridicaModel> salvarPessoaJuridica(@RequestBody @Valid PessoaJuridicaModel pessoaJuridica) throws ExceptionMentoriaJava {
		
		if (pessoaJuridica == null) {
			throw new ExceptionMentoriaJava("Pessoa juridica não pode ser vazio.");
		}
		
		if (pessoaJuridica.getTipo_pessoa() == null) {
			throw new ExceptionMentoriaJava("Informe o tipo da pessoa.");
		}
		
		if (pessoaJuridica.getId() == null && pessoaJuridicaRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
			throw new ExceptionMentoriaJava("Já existe pessoa cadastrada com o seguinte CNPJ: " + pessoaJuridica.getCnpj());
		}
		
		if (pessoaJuridica.getId() == null && pessoaJuridicaRepository.existeInscEstadualCadastrado(pessoaJuridica.getInscricao_estadual()) != null) {
			throw new ExceptionMentoriaJava("Já existe pessoa cadastrada com a seguinte Inscrição estadual: " + pessoaJuridica.getInscricao_municipal());
		}
		
		if (!ValidaCNPJ.isCNPJ(pessoaJuridica.getCnpj())) {
			throw new ExceptionMentoriaJava("CNPJ: " + pessoaJuridica.getCnpj() + " está inválido.");
		}
		
		if (pessoaJuridica.getId() == null || pessoaJuridica.getId() <= 0) {
			for(int i = 0; i < pessoaJuridica.getEnderecos().size(); i++) {
				CepDTO cepDTO = pessoaUsuarioService.consultaCep(pessoaJuridica.getEnderecos().get(i).getCep());
				
				pessoaJuridica.getEnderecos().get(i).setBairro(cepDTO.getBairro());
				pessoaJuridica.getEnderecos().get(i).setCidade(cepDTO.getLocalidade());
				pessoaJuridica.getEnderecos().get(i).setComplemento(cepDTO.getComplmento());
				pessoaJuridica.getEnderecos().get(i).setLogradouro(cepDTO.getLogradouro());
				pessoaJuridica.getEnderecos().get(i).setUf(cepDTO.getUf());
			}
		} else {
			for(int i = 0; i < pessoaJuridica.getEnderecos().size(); i++) {
				Optional<Enderecos> enderecoTemp = enderecoRepository.findById(pessoaJuridica.getEnderecos().get(i).getId());
				
				if (!enderecoTemp.get().getCep().equals(pessoaJuridica.getEnderecos().get(i).getCep())) {
					CepDTO cepDTO = pessoaUsuarioService.consultaCep(pessoaJuridica.getEnderecos().get(i).getCep());
					
					pessoaJuridica.getEnderecos().get(i).setBairro(cepDTO.getBairro());
					pessoaJuridica.getEnderecos().get(i).setCidade(cepDTO.getLocalidade());
					pessoaJuridica.getEnderecos().get(i).setComplemento(cepDTO.getComplmento());
					pessoaJuridica.getEnderecos().get(i).setLogradouro(cepDTO.getLogradouro());
					pessoaJuridica.getEnderecos().get(i).setUf(cepDTO.getUf());
				}
			}
		}
		
		pessoaJuridica = pessoaUsuarioService.salvarPessoaJuridica(pessoaJuridica);
		
		return new ResponseEntity<PessoaJuridicaModel>(pessoaJuridica, HttpStatus.OK);
	}
	
	@PostMapping(value = "/salvarPessoaFisica")
	public ResponseEntity<PessoaFisicaModel> salvarPessoaFisica(@RequestBody @Valid PessoaFisicaModel pessoaFisica) throws ExceptionMentoriaJava {
		
		if (pessoaFisica == null) {
			throw new ExceptionMentoriaJava("Pessoa física não pode ser vazio.");
		}
		
		if (pessoaFisica.getTipo_pessoa() == null) {
			pessoaFisica.setTipo_pessoa(TipoPessoa.FISICA.name());
		}
		
		if (pessoaFisica.getId() == null && pessoaFisicaRepository.existeCpfCadastrado(pessoaFisica.getCpf()) != null) {
			throw new ExceptionMentoriaJava("Já existe pessoa cadastrada com o seguinte CPF: " + pessoaFisica.getCpf());
		}
				
		if (!ValidaCPF.isCPF(pessoaFisica.getCpf())) {
			throw new ExceptionMentoriaJava("CPF: " + pessoaFisica.getCpf() + " está inválido.");
		}
		
		/*
		if (pessoaFisica.getId() == null || pessoaFisica.getId() <= 0) {
			for(int i = 0; i < pessoaFisica.getEnderecos().size(); i++) {
				CepDTO cepDTO = pessoaUsuarioService.consultCep(pessoaFisica.getEnderecos().get(i).getCep());
				
				pessoaFisica.getEnderecos().get(i).setBairro(cepDTO.getBairro());
				pessoaFisica.getEnderecos().get(i).setCidade(cepDTO.getLocalidade());
				pessoaFisica.getEnderecos().get(i).setComplemento(cepDTO.getComplmento());
				pessoaFisica.getEnderecos().get(i).setLogradouro(cepDTO.getLogradouro());
				pessoaFisica.getEnderecos().get(i).setUf(cepDTO.getUf());
			}
		} else {
			for(int i = 0; i < pessoaFisica.getEnderecos().size(); i++) {
				Optional<EnderecoModel> enderecoTemp = enderecoRepository.findById(pessoaFisica.getEnderecos().get(i).getId());
				
				if (!enderecoTemp.get().getCep().equals(pessoaFisica.getEnderecos().get(i).getCep())) {
					CepDTO cepDTO = pessoaUsuarioService.consultCep(pessoaFisica.getEnderecos().get(i).getCep());
					
					pessoaFisica.getEnderecos().get(i).setBairro(cepDTO.getBairro());
					pessoaFisica.getEnderecos().get(i).setCidade(cepDTO.getLocalidade());
					pessoaFisica.getEnderecos().get(i).setComplemento(cepDTO.getComplmento());
					pessoaFisica.getEnderecos().get(i).setLogradouro(cepDTO.getLogradouro());
					pessoaFisica.getEnderecos().get(i).setUf(cepDTO.getUf());
				}
			}
		}
		*/
		pessoaFisica = pessoaUsuarioService.salvarPessoaFisica(pessoaFisica);
		
		return new ResponseEntity<PessoaFisicaModel>(pessoaFisica, HttpStatus.OK);
	}
}
