package com.valois.controledeponto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializar componentes
        Button btn_entrar = findViewById(R.id.btn_entrar);


        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Olá usuário", Toast.LENGTH_SHORT);
                toast.show();

                Intent irParaHome = new Intent(MainActivity.this,ActivityHome.class);
                startActivity(irParaHome);
            }
        });

    }


}