package com.valois.controledeponto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valois.controledeponto.utils.Global;
import com.valois.controledeponto.modelo.Usuario;

public class ActivityHome extends AppCompatActivity {

    Usuario usuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        alert(Global.uid_usuario);
        inicializarComponentes();
        carregarDados();
        eventos();
    }



    /**INICIALIZA TODOS OS COMPONENTES VISUAIS DA TELA**/
    private void inicializarComponentes() {

    }

    private void eventos() {

    }

    /**CARREGA AS INFORMAÇÕES RECUPERADAS DA DATABASE**/
    private void carregarDados() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference().child(Global.no_usuario).child(Global.uid_usuario);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioLogado = snapshot.getValue(Usuario.class);
                setTitle("Bem Vindo \n" + usuarioLogado.getNome() + "!");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**CRIA O MENU DA ACTION BAR**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (usuarioLogado.isAdmin()) {
            getMenuInflater().inflate(R.menu.menu_activity_home_admin, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_activity_home, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**CONTROLA OS EVENTOS DOS BOTÕES DA ACTION BAR**/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_gerencia_usuarios:
                Intent i = new Intent(ActivityHome.this, GerenciaUsuariosActivity.class);
                startActivity(i);
                break;
            case R.id.menu_sair:
                System.exit(0);
                break;
        }

        return true;
    }

    /**CRIA UM ALERTA DO TIPO TOAST**/
    private void alert(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}