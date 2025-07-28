package dev.java10x.CadastroDeNinjas.Ninjas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class NinjaService {

    private static final Logger log = LoggerFactory.getLogger(NinjaService.class);
    private NinjaRepository ninjaRepository;
    private NinjaMapper ninjaMapper;

    public NinjaService(NinjaRepository ninjaRepository, NinjaMapper ninjaMapper) {
        this.ninjaRepository = ninjaRepository;
        this.ninjaMapper = ninjaMapper;
    }

    //Listar os ninjas
    public List<NinjaDTO> getTodosNinjas   (){
        List<NinjaModel> ninjas = ninjaRepository.findAll();
        return ninjas.stream()
                .map(ninjaMapper::map)
                .collect(Collectors.toList());
    }


    //Buscar Ninja por id
    public NinjaDTO getNinjasPorId(Long id){
        Optional<NinjaModel> ninjaPorId = ninjaRepository.findById(id);
        return ninjaPorId.map(ninjaMapper::map).orElse(null);
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
    public NinjaDTO atualizarNinja(Long id, NinjaDTO ninjaDTO){
        Optional<NinjaModel> ninjaExistente = ninjaRepository.findById(id);
        if (ninjaExistente.isPresent()){
            NinjaModel ninjaAtualizado = ninjaMapper.map(ninjaDTO);
            ninjaAtualizado.setId(id);

            NinjaModel ninjaSalvo = ninjaRepository.save(ninjaAtualizado);

            return ninjaMapper.map(ninjaSalvo);
        }

        return null;
    }
}
