package com.valois.controledeponto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_tela_login extends AppCompatActivity {

    Button btn_entrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);
        btn_entrar = findViewById(R.id.btn_login_entrar);

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent entrar = new Intent(Activity_tela_login.this, GerenciaUsuariosActivity.class);
                startActivity(entrar);
            }
        });
    }
}