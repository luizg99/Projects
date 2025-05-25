package dev.java10x.CadastroDeNinjas.Missoes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController //Definindo que é uma camada de controller
@RequestMapping("missoes")//Definindo que será mapeada a API
public class MissoesController {


    @GetMapping("/listar")
    public String listarMissoes(){
        return "Lista de missoes";
    }

    @PostMapping("/criar")
    public String criarMissao(){
        return "Missão criada com sucesso!";
    }

    @PutMapping("/alterar")
    public String alterarMissao(){
        return "Missão alterada com sucesso!";
    }

    @DeleteMapping("/deletar")
    public String deletarMissao(){
        return "Missão deletada com sucesso!";
    }
}
