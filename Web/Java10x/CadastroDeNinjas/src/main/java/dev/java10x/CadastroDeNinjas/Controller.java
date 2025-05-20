package dev.java10x.CadastroDeNinjas;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class Controller {

    @GetMapping("/boasVindas")
    public String boasVindas(){
        return "Boas Vindas";
    }

    @GetMapping("/boaNoite")
    public String boaNoite(){
        return "Boa Noite Nunes e Pedro!";
    }
}
