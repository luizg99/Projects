package com.example.testar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private CheckBox ckverde, ckBranco, ckVermelho;
    private TextView resultado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultado = findViewById(R.id.txtResultado);

        //CheckBox
        ckverde    = findViewById(R.id.ckVerde);
        ckVermelho = findViewById(R.id.ckVermelho);
        ckBranco   = findViewById(R.id.ckBranco);
    }

    public void checkBox(){
        String texto = "";
        if (ckverde.isChecked()){
            texto = "Verde selecionado - ";
        }
        if (ckBranco.isChecked()){
            texto = texto + "Branco selecionado - ";
        }
        if (ckVermelho.isChecked()){
            texto = texto + "Vermelho selecionado - ";
        }
        resultado.setText(texto);
    }

    public void enviar(View view){
        checkBox();
        /*
        EditText campoTexto = findViewById(R.id.eTexto);
        TextView resultado = findViewById(R.id.txtResultado);


        resultado.setText(texto);


         */
    }
}