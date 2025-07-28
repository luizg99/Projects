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

    //Adicionar ninja (CREATE)
    @PostMapping("/criar")
    public NinjaDTO criarNinja(@RequestBody NinjaDTO ninjaDto){
        return ninjaService.criarNinja(ninjaDto);
    }

    //Mostrar todos os ninjas (READ)
    @GetMapping("/todes")
    public List<NinjaDTO> mostrarTodosNinjas(){
        return ninjaService.getTodosNinjas();
    }

    //Mostrar ninja por id (READ)
    @GetMapping("/buscarId/{id}")
    public NinjaDTO mostrarTodosOsNinjasPorId(@PathVariable Long id){
        return ninjaService.getNinjasPorId(id);
    }

    //Alterar dados dos ninjas (UPDATE)
    @PutMapping("/alterarId/{id}")
    public NinjaDTO alterarNinjaPorId(@PathVariable Long id, @RequestBody NinjaDTO ninja){
        return ninjaService.atualizarNinja(id, ninja);
    }

    //Deletar ninja (DELETE)
    @DeleteMapping("/deletarId/{id}")
    public void deletarNinja(@PathVariable Long id){
        ninjaService.deletarNinja(id);
    }


}
