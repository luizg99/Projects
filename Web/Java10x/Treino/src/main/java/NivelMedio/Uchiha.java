package NivelMedio;

public class Uchiha extends Ninja{

    String habilidadeEspecial;

    public void MostrarHabilidadeEspecial(String pHabilidadeEspecial) {
        System.out.println("Habilidade Especial: " + pHabilidadeEspecial);
    }

    @Override
    public void mostrarInformacoes() {
        super.mostrarInformacoes();
        MostrarHabilidadeEspecial(habilidadeEspecial);
    }

    public int anoParaSeTornarRoka(int idadeMinimaParaRoka){
        return idadeMinimaParaRoka - idade;
    }

    public void SharinganAtivado(){
        System.out.println("Meu nome é: " + nome + ". O Sharingan Ativou, eu sou um Uchiha.");
    }
}
