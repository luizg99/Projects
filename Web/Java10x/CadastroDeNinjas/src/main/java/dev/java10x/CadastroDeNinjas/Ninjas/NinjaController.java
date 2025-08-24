package dev.java10x.CadastroDeNinjas.Ninjas;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Mensagens de boas vindas", description = "Essa mensagem dá boas vindas ao usuário quando acessada a rota.")
    public String boasVindas(){

        return "Boas Vindas";
    }

    //Adicionar ninja (CREATE)
    @PostMapping("/criar")
    public ResponseEntity<String> criarNinja(@RequestBody NinjaDTO ninjaDto){


        NinjaDTO ninjaDTO = ninjaService.criarNinja(ninjaDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("Ninja criado com sucesso, novo nija: " + ninjaDTO.getNome());
    }

    //Mostrar todos os ninjas (READ)
    @GetMapping("/todes")
    public ResponseEntity<List<NinjaDTO>> mostrarTodosNinjas(){

        List<NinjaDTO> ninjas =  ninjaService.getTodosNinjas();

        return ResponseEntity.ok(ninjas);


    }

    //Mostrar ninja por id (READ)
    @GetMapping("/buscarId/{id}")
    public ResponseEntity<?> mostrarTodosOsNinjasPorId(@PathVariable Long id) {

        NinjaDTO ninja = ninjaService.getNinjasPorId(id);

        if (ninja != null){
            return ResponseEntity.ok(ninja);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("O ninja com o id " + id + " não existe cadastrado.");
    }

    //Alterar dados dos ninjas (UPDATE)
    @PutMapping("/alterarId/{id}")
    public ResponseEntity<?> alterarNinjaPorId(@PathVariable Long id, @RequestBody NinjaDTO ninja){

        NinjaDTO ninjaModel = ninjaService.atualizarNinja(id, ninja);

        if  (ninjaModel != null){
            return ResponseEntity.ok(ninjaModel);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ninja com o id " + id + " não existe cadastrado.");
        }

    }

    //Deletar ninja (DELETE)
    @DeleteMapping("/deletarId/{id}")
    public ResponseEntity<String> deletarNinja(@PathVariable Long id){

        if(ninjaService.getNinjasPorId(id) != null){
            ninjaService.deletarNinja(id);
            return ResponseEntity.ok("Ninja " + id + " removido com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("O ninja " + id + " não foi encontrado, verifique!!");

        }


    }


}
