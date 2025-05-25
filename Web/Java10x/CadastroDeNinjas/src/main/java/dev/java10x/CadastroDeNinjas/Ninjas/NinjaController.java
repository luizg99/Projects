package dev.java10x.CadastroDeNinjas.Ninjas;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //Definindo que é uma camada de controller
@RequestMapping("ninjas")//Definindo que será mapeada a API
public class NinjaController {

    private NinjaService ninjaService;

    public NinjaController(NinjaService ninjaService) {
        this.ninjaService = ninjaService;
    }

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
    public List<NinjaModel> mostrarTodosNinjas(){
        return ninjaService.mostrarTodosNinjas();
    }

    //Mostrar ninja por id (READ)
    @GetMapping("/todosID")
    public List<NinjaModel> mostrarTodosOsNinjasPorId(){
        return null;
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
