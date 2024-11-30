package com.example.frases;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.time.Clock;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    int indice = 0;
    String[] frases = {
            "Se você faz algo mal feito agora por falta de tempo, o que te faz pensar que terá tempo para refazer depois?",
            "Mais são as pessoas que desistem, do que as que fracassam. Pra fracassar é preciso tentar até o final",
            "O verdadeiro conhecimento vem de dentro.",
            "Se alguém procura a saúde, pergunta-lhe primeiro se está disposto a evitar no futuro as causas da doença; em caso contrário, abstém-te de o ajudar."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView texto = findViewById(R.id.txtFrase);
        texto.setText(frases[0]);
    }

    public void fraseAnterior(View view){
        if (indice > 0) {
            indice--;

            TextView texto = findViewById(R.id.txtFrase);
            texto.setText(frases[indice]);
        }
    }

    public void proximaFrase(View view){

        if (indice < frases.length - 1) {
            indice++;

            TextView texto = findViewById(R.id.txtFrase);
            texto.setText(frases[indice]);
        }
    }



}