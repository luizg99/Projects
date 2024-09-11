package com.lojavirtual.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.xml.bind.DatatypeConverter;

import com.lojavirtual.model.dto.RelatorioProdutoAlertaEstoqueDTO;
import com.lojavirtual.service.ProdutoService;
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

import com.lojavirtual.model.ProdutoModel;
import com.lojavirtual.repository.ProdutoRepository;
import com.lojavirtual.service.EnvioEmailService;
import com.lojavirtual.util.ExceptionMentoriaJava;

import jakarta.validation.Valid;

@RestController
@RequestMapping("**/produto")
public class ProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private EnvioEmailService envioEmailService;

	@PostMapping // ** -> SERVE PRA IGNORAR O RESTANTE DA URL
	public ResponseEntity<ProdutoModel> salvarProduto(@RequestBody @Valid ProdutoModel produto) throws ExceptionMentoriaJava, MessagingException, IOException {
		if (produto.getEmpresa() == null || produto.getEmpresa().getId() <= 0) {
			throw new ExceptionMentoriaJava("Empresa responsável deve ser informada.");
		}
		
		if (produto.getTipoUnidade() == null || produto.getTipoUnidade().trim().isEmpty()) {
			throw new ExceptionMentoriaJava("Tipo da unidade deve ser informada.");
		}
		
		if (produto.getNome().length() < 10) {
			throw new ExceptionMentoriaJava("Nome do produto deve ter mais de 10 letras.");
		}
		
		if (produto.getId() == null) {
			List<ProdutoModel> produtos = produtoRepository.buscarProdutoNome(produto.getNome().toUpperCase(), produto.getEmpresa().getId());

			if (!produtos.isEmpty()) {
				throw new ExceptionMentoriaJava("Já existe produto com esse nome.");
			}
		}
		
		if (produto.getCategoriaProduto() == null || produto.getCategoriaProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("Categoria do produto deve ser informada.");
		}
		
		if (produto.getMarcaProduto() == null || produto.getMarcaProduto().getId() <= 0) {
			throw new ExceptionMentoriaJava("Marca do produto deve ser informada.");
		}
		
		if (produto.getQuantidadeEstoque() < 1 ) {
			throw new ExceptionMentoriaJava("Produto deve ter no mímimo 1 no estoque.");
		}
		
		if (produto.getImagens() == null || produto.getImagens().isEmpty() || produto.getImagens().size() == 0) {
			throw new ExceptionMentoriaJava("Deve ser informado imagens para o produto");
		}
		
		if (produto.getImagens().size() < 3) {
			throw new ExceptionMentoriaJava("Deve ser informado pelo menos 3 imagens para o produto");
		}
		
		if (produto.getImagens().size() > 6) {
			throw new ExceptionMentoriaJava("Deve ser informado no máximo 6 imagens para o produto");
		}
		
		if (produto.getId() == null) {
			for (int i = 0; i < produto.getImagens().size(); i++) {
				produto.getImagens().get(i).setProduto(produto);
				produto.getImagens().get(i).setEmpresa(produto.getEmpresa());
				
				String base64Image = "";
				
				if (produto.getImagens().get(i).getImagemOriginal().contains("data:image")) {
					base64Image = produto.getImagens().get(i).getImagemOriginal().split(",")[1];
				} else {
					base64Image = produto.getImagens().get(i).getImagemOriginal();
				}
				
				byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
				
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
				
				if (bufferedImage != null) {
					int type = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
					int largura = Integer.parseInt("800");
					int altura = Integer.parseInt("600");
					
					BufferedImage resizedImagem = new BufferedImage(largura, altura, type);
					Graphics2D g = resizedImagem.createGraphics();
					g.drawImage(bufferedImage, 0, 0, largura, altura, null);
					g.dispose();
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(resizedImagem, "png", baos);
					
					String miniImgBase64 = "data:image/png;base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
					produto.getImagens().get(i).setImagemMiniatura(miniImgBase64);
					
					bufferedImage.flush();
					resizedImagem.flush();
					baos.flush();
					baos.close();
				}
			}
		}

		ProdutoModel produtoSalvo = produtoRepository.save(produto);
		
		if (produto.getAlertaQuantidadeEstoque() && produto.getQuantidadeEstoque() < produto.getQuantidadeAlertaEstoque()) {
			StringBuilder html = new StringBuilder();
			html.append("<h2>")
				.append("Produto: " + produto.getNome())
				.append(" com estoque baixo: " + produto.getQuantidadeEstoque())
				.append("<p>Id Prod.: ").append(produto.getId()).append("</p>");
			
			if (produto.getEmpresa().getEmail() != null) {
				envioEmailService.enviarEmailHtml("Produto sem estoque", html.toString(), produto.getEmpresa().getEmail());
			}
		}

		return new ResponseEntity<ProdutoModel>(produtoSalvo, HttpStatus.OK);
	}

	@PostMapping(value = "**/deleteProduto")
	public ResponseEntity<String> deleteAcesso(@RequestBody ProdutoModel produto) {
		produtoRepository.deleteById(produto.getId());

		return new ResponseEntity<String>("Produto removido.", HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deleteProdutoPorId(@PathVariable Long id) {
		produtoRepository.deleteById(id);

		return new ResponseEntity<String>("Produto removido.", HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<ProdutoModel> obterProdutoPorId(@PathVariable Long id) throws ExceptionMentoriaJava {
		ProdutoModel produto = produtoRepository.findById(id).orElse(null);

		if (produto == null) {
			throw new ExceptionMentoriaJava("Não existe produto cadastrado com o código: " + id);
		}

		return new ResponseEntity<ProdutoModel>(produto, HttpStatus.OK);
	}

	@GetMapping(value = "/buscar/{nome}")
	public ResponseEntity<List<ProdutoModel>> buscarProdutoPorNome(@PathVariable String nome) {
		List<ProdutoModel> produtos = produtoRepository.buscarProdutoNome(nome);

		return new ResponseEntity<List<ProdutoModel>>(produtos, HttpStatus.OK);
	}

	@GetMapping(value = "/relatorioAlertaEstoque")
	public ResponseEntity<List<RelatorioProdutoAlertaEstoqueDTO>> relatorioAlertaEstoqueBaixo() {
		List<RelatorioProdutoAlertaEstoqueDTO> produtos = new ArrayList<>();
		produtos = produtoService.gerarRelatorioAlertaEstoque();

		return new ResponseEntity<>(produtos, HttpStatus.OK);
	}
}