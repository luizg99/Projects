package com.example.myapplication.Model;

public class AnalisadorQuestao {
    public  boolean isRespostaCorreta(Questao questao, double resposta){
        return  questao.getRespostaCorreta() == resposta;
    }
}
