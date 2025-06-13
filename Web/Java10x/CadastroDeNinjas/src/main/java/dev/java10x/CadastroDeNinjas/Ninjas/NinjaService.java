package dev.java10x.CadastroDeNinjas.Ninjas;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NinjaService {

    private NinjaRepository ninjaRepository;
    private NinjaMapper ninjaMapper;

    public NinjaService(NinjaRepository ninjaRepository, NinjaMapper ninjaMapper) {
        this.ninjaRepository = ninjaRepository;
        this.ninjaMapper = ninjaMapper;
    }

    //Listar os ninjas
    public List<NinjaDTO> getTodosNinjas(){
        List<NinjaModel> ninjas = ninjaRepository.findAll();
        return ninjas.stream()

        return ninjaRepository.findAll();
    }


    //Buscar Ninja por id
    public NinjaModel getNinjasPorId(Long id){
        Optional<NinjaModel> ninjaPorId = ninjaRepository.findById(id);
        return ninjaPorId.orElse(null);
    }

    //Criar ninja
    public NinjaDTO criarNinja(NinjaDTO ninjaDto){
        NinjaModel ninja = ninjaMapper.map(ninjaDto);
        ninjaRepository.save(ninja);

        return ninjaMapper.map(ninja);
    }

    //Deletar ninja - tem que ser metodo void
    public void deletarNinja(Long id){
        ninjaRepository.deleteById(id);
    }

    //Atualizando o ninja
    public NinjaModel atualizarNinja(Long id, NinjaModel ninjaAtualizado){
        if (ninjaRepository.existsById(id)){
            ninjaAtualizado.setId(id);
            return ninjaRepository.save(ninjaAtualizado);
        }

        return null;
    }
}
