package com.example.frases;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gerarNovaFrase(View view){
        String[] frases = {
                "Se você faz algo mal feito agora por falta de tempo, o que te faz pensar que terá tempo para refazer depois?",
                "O verdadeiro conhecimento vem de dentro.",
                "Se alguém procura a saúde, pergunta-lhe primeiro se está disposto a evitar no futuro as causas da doença; em caso contrário, abstém-te de o ajudar."
        };

        int numero = new Random().nextInt(3);


        TextView texto = findViewById(R.id.txtFrase);
        texto.setText(frases[numero]);
    }



}