package com.example.myapplication.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.example.myapplication.Model.AnalisadorQuestao;
import com.example.myapplication.Model.Questao;
import com.example.myapplication.Model.QuestaoRepositorio;
import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {
    public static final String INDICE_QUESTAO = "INDICE_QUESTAO";
    private final Locale locale = new Locale("pt", "BR");
    private QuestaoRepositorio repositorio = new QuestaoRepositorio();
    private int indiceQuestao = 0;
    private TextView textViewTextoPergunta;
    private Button botaoResposta1;
    private Button botaoResposta2;
    String mensagem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String resposta = ((Button)v).getText().toString();
                AnalisadorQuestao analisadorQuestao = new AnalisadorQuestao();
                Questao questao = repositorio.getListaQuestoes().get(indiceQuestao);

                if (analisadorQuestao.isRespostaCorreta(questao, Double.valueOf(resposta))){
                    mensagem = "Parabéns, resposta correta!";
                }
                else {
                    mensagem = "Resposta incorreta!";
                }

                Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_SHORT).show();
            }

        };

        //instanciando os botões
        TextView textViewTextoPergunta = findViewById(R.id.texto_pergunta_textview);
        Button botaoResposta1 = findViewById(R.id.opcao1_button);
        Button botaoResposta2 = findViewById(R.id.opcao2_button);
        Button botaoProximaPergunta = findViewById(R.id.proxima_pergunta_button);

        exibirQuestao(indiceQuestao);

        //chamando o listner para obter a resposta correta ou incorreta
        botaoResposta1.setOnClickListener(listener);
        botaoResposta2.setOnClickListener(listener);

        View.OnClickListener listenerProximaPergunta = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indiceQuestao++;

                if (indiceQuestao >= repositorio.getListaQuestoes().size()) {
                    indiceQuestao = 0;
                }

                exibirQuestao(indiceQuestao);

            }
        };

        botaoProximaPergunta.setOnClickListener(listenerProximaPergunta);

    }

    private void exibirQuestao(final int indiceQuestao) {
        Questao questao = repositorio.getListaQuestoes().get(indiceQuestao);
        textViewTextoPergunta.setText(questao.getTexto());
        botaoResposta1.setText(String.valueOf(questao.getRespostaCorreta()));
        botaoResposta2.setText(String.valueOf(questao.getRespostaIncorreta() ));
    }

}