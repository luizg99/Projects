package com.lojavirtual.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lojavirtual.model.ImagemProdutoModel;
import com.lojavirtual.model.dto.ImagemProdutoDTO;
import com.lojavirtual.repository.ImagemProdutoRepository;
import com.lojavirtual.util.ExceptionMentoriaJava;

@RestController
@RequestMapping("**/imagemProduto")
public class ImagemProdutoController {

	@Autowired
	private ImagemProdutoRepository imagemProdutoRepository;
	
	@PostMapping  // ** -> SERVE PRA IGNORAR O RESTANTE DA URL
	public ResponseEntity<ImagemProdutoDTO> salvarImagemProduto(@RequestBody ImagemProdutoModel imagemProduto) throws ExceptionMentoriaJava {
		ImagemProdutoModel imagemProdutoSalvo = imagemProdutoRepository.save(imagemProduto);
		
		ImagemProdutoDTO imagemProdutoDTO = new ImagemProdutoDTO();
		
		imagemProdutoDTO.setId(imagemProdutoSalvo.getId());
		imagemProdutoDTO.setEmpresa(imagemProdutoSalvo.getEmpresa().getId());
		imagemProdutoDTO.setProduto(imagemProdutoSalvo.getProduto().getId());
		imagemProdutoDTO.setImagemOriginal(imagemProdutoSalvo.getImagemOriginal());
		imagemProdutoDTO.setImagemMiniatura(imagemProdutoSalvo.getImagemMiniatura());
				
		return new ResponseEntity<ImagemProdutoDTO>(imagemProdutoDTO, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/imagensProduto/{produtoId}")  
	public ResponseEntity<String> deleteImagensProduto(@PathVariable Long produtoId) {
		if (imagemProdutoRepository.buscarImagensProduto(produtoId).isEmpty()) {
			return new ResponseEntity<String>("As imagens desse produto já foram removidas.", HttpStatus.OK);
		}
		
		imagemProdutoRepository.deletarImagensProduto(produtoId);
	
		return new ResponseEntity<String>("Imagens do produto removidas.", HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}")  
	public ResponseEntity<String> deleteImagemProdutoPorId(@PathVariable Long id) {		
		imagemProdutoRepository.deleteById(id);
	
		return new ResponseEntity<String>("Imagem do produto removida.", HttpStatus.OK);
	}
	
	@GetMapping(value = "/{produtoId}")  
	public ResponseEntity<List<ImagemProdutoDTO>> buscarImagensProduto(@PathVariable Long produtoId) {
		List<ImagemProdutoModel> imagensProdutos = imagemProdutoRepository.buscarImagensProduto(produtoId);
		
		List<ImagemProdutoDTO> dtos = new ArrayList<ImagemProdutoDTO>();
		
		for(ImagemProdutoModel imagemProduto : imagensProdutos) {
			
			ImagemProdutoDTO dto = new ImagemProdutoDTO();
			
			dto.setId(imagemProduto.getId());
			dto.setEmpresa(imagemProduto.getEmpresa().getId());
			dto.setProduto(imagemProduto.getProduto().getId());
			dto.setImagemOriginal(imagemProduto.getImagemOriginal());
			dto.setImagemMiniatura(imagemProduto.getImagemMiniatura());
			
			dtos.add(dto);
		}
		
		return new ResponseEntity<List<ImagemProdutoDTO>>(dtos, HttpStatus.OK);
	}

}
