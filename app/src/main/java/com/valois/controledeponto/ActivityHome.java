package com.valois.controledeponto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valois.controledeponto.utils.Global;
import com.valois.controledeponto.modelo.Usuario;

public class ActivityHome extends AppCompatActivity {

    Usuario usuarioLogado;

    Button btn_home, btn_add_ponto, btn_historico, btn_gerenciar;
    ImageView img01, img02,img03,img04;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        alert(Global.uid_usuario);
        inicializarComponentes();
        carregarDados();
        eventos();


    }

    /**
     * INICIALIZA TODOS OS COMPONENTES VISUAIS DA TELA
     **/
    private void inicializarComponentes() {
        btn_home = findViewById(R.id.btn_home);
        btn_add_ponto = findViewById(R.id.btn_marcar_ponto);
        btn_historico = findViewById(R.id.btn_espelho);
        btn_gerenciar = findViewById(R.id.btn_admin);

        img01 = findViewById(R.id.img_01);
        img02 = findViewById(R.id.img_02);
        img03 = findViewById(R.id.img_03);
        img04 = findViewById(R.id.img_04);
    }

    private void eventos() {
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img01.setVisibility(View.VISIBLE);
                img02.setVisibility(View.GONE);
                img03.setVisibility(View.GONE);
                img04.setVisibility(View.GONE);
                //alert("teste");
            }
        });

        btn_add_ponto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img01.setVisibility(View.GONE);
                img02.setVisibility(View.VISIBLE);
                img03.setVisibility(View.GONE);
                img04.setVisibility(View.GONE);
            }
        });

        btn_historico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img01.setVisibility(View.GONE);
                img02.setVisibility(View.GONE);
                img03.setVisibility(View.VISIBLE);
                img04.setVisibility(View.GONE);
            }
        });

        btn_gerenciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img01.setVisibility(View.GONE);
                img02.setVisibility(View.GONE);
                img03.setVisibility(View.GONE);
                img04.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * CARREGA AS INFORMAÇÕES RECUPERADAS DA DATABASE
     **/
    private void carregarDados() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference().child(Global.no_usuario).child(Global.uid_usuario);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioLogado = snapshot.getValue(Usuario.class);
                setTitle("Bem Vindo " + usuarioLogado.getNome() + " !");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * CRIA O MENU DA ACTION BAR
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (usuarioLogado.isAdmin()) {
            getMenuInflater().inflate(R.menu.menu_activity_home_admin, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_activity_home, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * CONTROLA OS EVENTOS DOS BOTÕES DA ACTION BAR
     **/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_gerencia_usuarios:
                Intent i = new Intent(ActivityHome.this, GerenciaUsuariosActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.menu_sair:
                System.exit(0);
                break;
        }

        return true;
    }

    /**
     * CRIA UM ALERTA DO TIPO TOAST
     **/
    private void alert(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}