package com.example.myapplication.Model;

public class Questao {

    private String texto;
    private double respostaCorreta, respostaIncorreta;
    public  Questao(String texto, double respostaCorreta, double respostaIncorreta) {
        this.texto = texto;
        this.respostaCorreta = respostaCorreta;
        this.respostaIncorreta = respostaIncorreta;
    }

    public String getTexto() {
        return texto;
    }

    public double getRespostaCorreta() {
        return respostaCorreta;
    }

    public double getRespostaIncorreta() {
        return respostaIncorreta;
    }

}
