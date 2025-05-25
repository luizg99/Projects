package dev.java10x.CadastroDeNinjas.Ninjas;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class NinjaController {

    @GetMapping("/boasVindas")
    public String boasVindas(){
        return "Boas Vindas";
    }

    @GetMapping("/boaNoite")
    public String boaNoite(){
        return "Boa Noite Nunes e Pedro!";
    }


    //Adicionar ninja (CREATE)
    @PostMapping("/criar")
    public String criarNinja(){
        return "Ninja Criado";
    }

    //Mostrar todos os ninjas (READ)
    @GetMapping("/todes")
    public String mostrarTodosNinjas(){
        return "Todes Ninjas";
    }

    //Mostrar ninja por id (READ)
    @GetMapping("/todosID")
    public String mostrarTodosOsNinjasPorId(){
        return "Todes Ninjas ID";
    }

    //Alterar dados dos ninjas (UPDATE)
    @PutMapping("/alterarId")
    public String alterarNinjaPorId(){
        return "Ninja Alterado";
    }

    //Deletar ninja (DELETE)
    @DeleteMapping("/deletarId")
    public String deletarNinja(){
        return "Ninja Deletado";
    }
}
