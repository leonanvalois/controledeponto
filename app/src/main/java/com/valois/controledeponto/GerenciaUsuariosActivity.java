package com.valois.controledeponto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valois.controledeponto.Utils.Conexao;
import com.valois.controledeponto.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GerenciaUsuariosActivity extends AppCompatActivity {

    EditText edt_nome, edt_email, edt_senha;
    ListView list_usuarios;
    private FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Usuario> listUsuario = new ArrayList<Usuario>();
    private ArrayAdapter<Usuario> arrayAdapterUsuario;

    Usuario usuarioSelecionado;

    String no_principal = "Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerencia_usuarios);

        edt_nome = findViewById(R.id.edt_nome);
        edt_email = findViewById(R.id.edt_email);
        edt_senha = findViewById(R.id.edt_senha);
        list_usuarios = findViewById(R.id.list_usuarios);
        Toast.makeText(GerenciaUsuariosActivity.this, "ola", Toast.LENGTH_LONG);
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

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = Conexao.getFirebaseAuth();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new AlertDialog.Builder(GerenciaUsuariosActivity.this)
                .setTitle("Você será desconectado")
                .setMessage("Tem certeza que deseja sair do sistema?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void eventoDatabase() {
        databaseReference.child(no_principal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsuario.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Usuario u = objSnapshot.getValue(Usuario.class);
                    listUsuario.add(u);
                }

                arrayAdapterUsuario = new ArrayAdapter<Usuario>(GerenciaUsuariosActivity.this, android.R.layout.simple_list_item_1, listUsuario);
                list_usuarios.setAdapter(arrayAdapterUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(GerenciaUsuariosActivity.this);
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
            case R.id.menu_atualizar:
                if (edt_email.getText().toString().isEmpty() ) {
                    alert("Preencha os campos ou seleciona um usuário para editar.");
                } else {
                    Usuario uAtualiza = new Usuario();
                    if (usuarioSelecionado == null){
                        uAtualiza.setUid(UUID.randomUUID().toString());
                    } else  {
                        uAtualiza.setUid(usuarioSelecionado.getUid());
                    }
                    uAtualiza.setNome(edt_nome.getText().toString().trim());
                    uAtualiza.setEmail(edt_email.getText().toString().trim());
                    uAtualiza.setSenha(edt_senha.getText().toString().trim());
                    databaseReference.child(no_principal).child(uAtualiza.getUid()).setValue(uAtualiza);
                    limparCampos();
                }
                break;

            case R.id.menu_apagar:
                if (edt_email.getText().toString().isEmpty() ) {
                    alert("Não há nada pra apagar");
                } else {
                    new AlertDialog.Builder(GerenciaUsuariosActivity.this)
                            .setTitle("O usuário " + edt_nome.getText() + " será excluído.")
                            .setMessage("Deseja realmente excluir o usuário?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Usuario uDelete = new Usuario();
                                    uDelete.setUid(usuarioSelecionado.getUid());
                                    databaseReference.child(no_principal).child(uDelete.getUid()).removeValue();
                                    alert("Usuário excluído com sucesso.");
                                    limparCampos();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
                break;
        }

        return true;
    }

    private void criarUsuario(String email, String senha) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(GerenciaUsuariosActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    alert("Usuário cadastrado com sucesso");
                    Intent i = new Intent(getApplicationContext(), Activity_tela_login.class);
                    startActivity(i);
                    finish();
                } else {
                    alert(task.getResult().toString());
                }
            }
        });
    }

    private void alert(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void limparCampos() {
        edt_nome.setText("");
        edt_email.setText("");
        edt_senha.setText("");
        usuarioSelecionado = null;
    }
}