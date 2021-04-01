package com.valois.controledeponto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valois.controledeponto.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText edt_nome, edt_email, edt_senha;
    ListView list_usuarios;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Usuario> listUsuario = new ArrayList<Usuario>();
    private ArrayAdapter<Usuario> arrayAdapterUsuario;

    Usuario usuarioSelecionado ;

    String no_principal = "Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_nome = findViewById(R.id.edt_nome);
        edt_email = findViewById(R.id.edt_email);
        edt_senha = findViewById(R.id.edt_senha);
        list_usuarios = findViewById(R.id.list_usuarios);

        inicializarFirebase();

        eventoDatabase();

       list_usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               usuarioSelecionado = (Usuario) parent.getItemAtPosition(position);
               edt_nome.setText(usuarioSelecionado.getNome());
               edt_email.setText(usuarioSelecionado.getEmail());
               edt_senha.setText(usuarioSelecionado.getSenha());
           }
       });

    }

    private void eventoDatabase() {
        databaseReference.child(no_principal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsuario.clear();
                for (DataSnapshot objSnapshot: snapshot.getChildren()){
                    Usuario u = objSnapshot.getValue(Usuario.class);
                    listUsuario.add(u);
                }

                arrayAdapterUsuario = new ArrayAdapter<Usuario>(MainActivity.this, android.R.layout.simple_list_item_1, listUsuario);
                list_usuarios.setAdapter(arrayAdapterUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_novo:
                Usuario usuario = new Usuario();
                usuario.setUid(UUID.randomUUID().toString());
                usuario.setNome(edt_nome.getText().toString());
                usuario.setEmail(edt_email.getText().toString());
                usuario.setSenha(edt_senha.getText().toString());
                databaseReference.child(no_principal).child(usuario.getUid()).setValue(usuario);
                limparCampos();
                break;

            case R.id.menu_salvar:
                Usuario uAtualiza = new Usuario();
                uAtualiza.setUid(usuarioSelecionado.getUid());
                uAtualiza.setNome(edt_nome.getText().toString().trim());
                uAtualiza.setEmail(edt_email.getText().toString().trim());
                uAtualiza.setSenha(edt_senha.getText().toString().trim());
                databaseReference.child(no_principal).child(uAtualiza.getUid()).setValue(uAtualiza);
                limparCampos();
                break;

            case R.id.menu_apagar:
                Usuario uDelete = new Usuario();
                uDelete.setUid(usuarioSelecionado.getUid());
                databaseReference.child(no_principal).child(uDelete.getUid()).removeValue();
                limparCampos();
                break;
        }

        return true;
    }

    private void limparCampos() {
        edt_nome.setText("");
        edt_email.setText("");
        edt_senha.setText("");
    }
}