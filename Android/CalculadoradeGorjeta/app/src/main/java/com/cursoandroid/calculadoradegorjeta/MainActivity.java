package com.cursoandroid.calculadoradegorjeta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText editValor;
    private TextView textPorcentagem;
    private TextView textGorgeta;
    private TextView textTotal;
    private SeekBar seekBarGorgeta;

    private double porcentagem = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editValor       = findViewById(R.id.edtValor);
        textGorgeta     = findViewById(R.id.txtGorjeta);
        textPorcentagem = findViewById(R.id.txtPorcentagem);
        textTotal       = findViewById(R.id.txtTotal);
        seekBarGorgeta  = findViewById(R.id.sbGorjeta);

        //adcionar listner

        seekBarGorgeta.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                porcentagem = progress;
                textPorcentagem.setText( Math.round(porcentagem) + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        }
    }


    public void calcular(){
        String valorRecuperado = editValor.getText().toString();

        if( valorRecuperado == null || valorRecuperado.equals("") ){
            Toast.makeText(
                    getApplicationContext(),
                    "Digite um valor primeiro",
                    Toast.LENGTH_LONG
            ).show();
        }
        else{
            //converte string para double
            double valorDigitado = Double.parseDouble( valorRecuperado );

            //calculo da gorgeta
            double gorjeta = valorDigitado * (porcentagem/100);
            double valorTotal = gorjeta + valorDigitado;

            textGorgeta.setText("R$ " + Math.round(gorjeta)  );
            textTotal.setText("R$ " + Math.round(valorTotal)  );
        }
    }

}
