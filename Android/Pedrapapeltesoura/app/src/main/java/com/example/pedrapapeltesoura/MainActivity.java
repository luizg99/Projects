package com.example.pedrapapeltesoura;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

//    void zerarEAguardar(){
//
//            ImageView minhaEscolha1 = findViewById(R.id.imMinhaEscolha);
//            ImageView imageResultado = findViewById(R.id.imResultado);
//            TextView textoResultado = findViewById(R.id.txtResultado);
//
//            textoResultado.setText("JOGANDO, AGUARDE O RESULTADO!");
//
//            minhaEscolha1.setImageResource(R.drawable.padrao);
//            imageResultado.setImageResource(R.drawable.padrao);
//    }

    public void selecionadoPedra(View view){
//        zerarEAguardar();
        ImageView minhaEscolha = findViewById(R.id.imMinhaEscolha);
        minhaEscolha.setImageResource(R.drawable.pedra);
        this.opcaoSelecionada("pedra");
    }
    public void selecionadoPapel(View view){
//        zerarEAguardar();
        ImageView minhaEscolha = findViewById(R.id.imMinhaEscolha);
        minhaEscolha.setImageResource(R.drawable.papel);
        this.opcaoSelecionada("papel");
    }

    public void selecionadoTesoura(View view){
//        zerarEAguardar();
        ImageView minhaEscolha = findViewById(R.id.imMinhaEscolha);
        minhaEscolha.setImageResource(R.drawable.tesoura);
        this.opcaoSelecionada("tesoura");
    }

    public void opcaoSelecionada(String opcaoSelecionada){
        ImageView imageResultado = findViewById(R.id.imResultado);
        TextView textoResultado = findViewById(R.id.txtResultado);

//        try {
//            new Thread().sleep(2000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        };

        int numero = new Random().nextInt(3);
        String[] escolhaDoApp = {"pedra", "papel", "tesoura"};
        String opcaoApp = escolhaDoApp[numero];

        switch (opcaoApp) {
            case "pedra":
                imageResultado.setImageResource(R.drawable.pedra);
                break;
            case "papel":
                imageResultado.setImageResource(R.drawable.papel);
                break;
            case "tesoura":
                imageResultado.setImageResource(R.drawable.tesoura);
                break;
        }

        if(
                (opcaoApp == "tesoura" && opcaoSelecionada == "papel") ||
                (opcaoApp == "papel" && opcaoSelecionada == "pedra") ||
                (opcaoApp == "pedra" && opcaoSelecionada == "tesoura")
        ){//App ganhador
            textoResultado.setText("Você PERDEU :(");
        }else if(
                (opcaoSelecionada == "tesoura" && opcaoApp == "papel") ||
                (opcaoSelecionada == "papel" && opcaoApp == "pedra") ||
                (opcaoSelecionada == "pedra" && opcaoApp == "tesoura")
        ){//Usuario
            textoResultado.setText("Você GANHOU :D");

        }else{//Empate
            textoResultado.setText("Empatamos ;)");
        }

        opcaoSelecionada = "";
    }
}