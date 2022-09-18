package com.example.testar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.lang.Thread;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBarHorizontal;
    private ProgressBar progressBarCircular;
    private int progresso = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarHorizontal = findViewById(R.id.pbHorizontal);
        //progressBarCircular = findViewById(R.id.pbCircular);

    }

    public void carregarProgressBar(){
        progressBarHorizontal.incrementProgressBy(this.progresso + 1);
    }

    public void carregar(View view) throws InterruptedException {
        int i;
        int n = 10;
        int vetor[] = new int[10];

        for (i=0; i<n; i++) {
            progressBarHorizontal.getProgress();
            carregarProgressBar();
            Thread.sleep(1000);
        }
    }

}