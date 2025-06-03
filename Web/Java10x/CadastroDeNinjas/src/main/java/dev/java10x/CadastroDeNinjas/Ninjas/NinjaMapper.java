package dev.java10x.CadastroDeNinjas.Ninjas;

import org.springframework.stereotype.Component;

@Component
public class NinjaMapper {

    public NinjaModel map(NinjaDTO ninjaDTO) {
        if (ninjaDTO == null) {
            return null;
        }

        NinjaModel ninjaModel = new NinjaModel();
        ninjaModel.setId(ninjaDTO.getId());
        ninjaModel.setNome(ninjaDTO.getNome());
        ninjaModel.setEmail(ninjaDTO.getEmail());
        ninjaModel.setIdade(ninjaDTO.getIdade());
        ninjaModel.setImgUrl(ninjaDTO.getImgUrl());
        ninjaModel.setRank(ninjaDTO.getRank());
        ninjaModel.setCaracteristicas(ninjaDTO.getCaracteristicas());
        ninjaModel.setMissoes(ninjaDTO.getMissoes());

        return ninjaModel;
    }

    public NinjaDTO map(NinjaModel ninjaModel) {
        if (ninjaModel == null) {
            return null;
        }

        return new NinjaDTO(
                ninjaModel.getId(),
                ninjaModel.getNome(),
                ninjaModel.getEmail(),
                ninjaModel.getIdade(),
                ninjaModel.getImgUrl(),
                ninjaModel.getRank(),
                ninjaModel.getCaracteristicas(),
                ninjaModel.getMissoes()
        );
    }
}
