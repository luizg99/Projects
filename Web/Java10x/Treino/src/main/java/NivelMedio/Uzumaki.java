package NivelMedio;

public class Uzumaki extends Ninja{
    public void AtivarModoSabio(){
        System.out.println("Modo sabio ativado!!!!");
    }

    @Override
    public void habilidadeEspecial() {
        System.out.println("Sou o " + nome + ". esse é meu ataque Uzumaki");
    }
}
