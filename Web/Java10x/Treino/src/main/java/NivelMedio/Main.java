package NivelMedio;

public class Main {

    public static void main(String[] args) {
        //Boruto

        Boruto Boruto = new Boruto();

        Boruto.nome = "Boruto";
        Boruto.aldeia = "Aldeia";
        Boruto.idade = 15;

        Boruto.AtivarModoSabio();
        Boruto.ativarKarma();
        Boruto.atvarJougan();




        //Objeto 1

        Uchiha Sasuke = new Uchiha();

        Sasuke.nome = "Sasuke";
        Sasuke.aldeia = "Aldeia";
        Sasuke.idade = 18;

        System.out.println("nome: " + Sasuke.nome);
        Sasuke.Ratatatatatatatat();


        int TempoFalta = Sasuke.anoParaSeTornarRoka(20);
        System.out.println("Falta " + TempoFalta + " anos para se toranar ROKA");

        //Objeto 2

        Uzumaki Naruto = new Uzumaki();

        Naruto.nome = "Naruto";
        Naruto.aldeia = "Aldeia";
        Naruto.idade = 18;

        System.out.println("Nome: " + Naruto.nome);

        Naruto.AtivarModoSabio();
        Naruto.Ratatatatatatatat();

        //Objeto 3
        Harumo Sakura = new Harumo();

        Sakura.nome = "Naruto";
        Sakura.aldeia = "Aldeia";
        Sakura.idade = 18;

        System.out.println("Nome: " + Naruto.nome);

        Sakura.AtivarCura();
        Sakura.Ratatatatatatatat();

        //Objeto 3
        Hyuga Hinata = new Hyuga();

        Hinata.nome = "Hinata";
        Hinata.aldeia = "Aldeia";
        Hinata.idade = 18;

        Hinata.ByakuganAtivado();


    }

}
