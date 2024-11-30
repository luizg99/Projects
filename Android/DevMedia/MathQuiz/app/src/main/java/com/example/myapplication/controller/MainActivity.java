package com.example.myapplication.controller;

import androidx.annotation.NonNull;
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

        textViewTextoPergunta = findViewById(R.id.texto_pergunta_textview);
        botaoResposta1 = findViewById(R.id.opcao1_button);
        botaoResposta2 = findViewById(R.id.opcao2_button);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String resposta = ((Button)v).getText().toString();

                AnalisadorQuestao analisadorQuestao = new AnalisadorQuestao();

                try {
                    NumberFormat format = NumberFormat.getInstance(locale);
                    Number number = format.parse(resposta);

                    if (analisadorQuestao.isRespostaCorreta(questao, number.doubleValue())) {
                        mensagem = "Parabéns, resposta correta!";
                    } else {
                        mensagem = "Aah, resposta errada :(";
                    }
                } catch(ParseException e) {
                    mensagem = e.getMessage();
                }

                Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_SHORT).show();
            }

        };

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

        Button botaoProximaPergunta = findViewById(R.id.proxima_pergunta_button);
        botaoProximaPergunta.setOnClickListener(listenerProximaPergunta);

        if(savedInstanceState != null) {
            indiceQuestao = savedInstanceState.getInt(INDICE_QUESTAO);
        };

        exibirQuestao(indiceQuestao);
    }
    Questao questao = repositorio.getListaQuestoes().get(indiceQuestao);
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INDICE_QUESTAO, indiceQuestao);
    }

    private void exibirQuestao(final int indiceQuestao) {
        Questao questao = repositorio.getListaQuestoes().get(indiceQuestao);
        textViewTextoPergunta.setText(questao.getTexto());

        String respostaCorreta = String.format(locale, "%.2f", questao.getRespostaCorreta());
        String respostaIncorreta = String.format(locale, "%.2f", questao.getRespostaIncorreta());
        botaoResposta1.setText(respostaCorreta);
        botaoResposta2.setText(respostaIncorreta);
    }

}