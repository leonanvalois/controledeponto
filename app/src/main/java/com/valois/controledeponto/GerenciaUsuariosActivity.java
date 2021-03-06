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
import com.valois.controledeponto.modelo.Usuario;
import com.valois.controledeponto.utils.Conexao;
import com.valois.controledeponto.utils.Global;

import java.util.ArrayList;
import java.util.List;

public class GerenciaUsuariosActivity extends AppCompatActivity {

    private EditText edt_nome, edt_email, edt_senha;
    private ListView list_usuarios;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Usuario> listUsuarios = new ArrayList<Usuario>();
    private ArrayAdapter<Usuario> arrayAdapterUsuario;
    Usuario usuarioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerencia_usuarios);
        inicializarComponentes();
        inicializarFirebase();
        eventoDatabase();
        eventos();
        alert(Global.uid_usuario);
    }

    /**INICIALIZA TODOS OS COMPONENTES VISUAIS DA TELA**/
    private void inicializarComponentes() {
        edt_nome        = findViewById(R.id.edt_nome);
        edt_email       = findViewById(R.id.edt_email);
        edt_senha       = findViewById(R.id.edt_senha);
        list_usuarios   = findViewById(R.id.list_usuarios);
    }
    /**M??TODO RESPONS??VEL POR ARMAZENAR OS EVENTOS DE CLICK DOS COMPONENTES**/
    private void eventos() {
        list_usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usuarioSelecionado = (Usuario) parent.getItemAtPosition(position);
                edt_nome.setText(usuarioSelecionado.getNome());
                edt_email.setText(usuarioSelecionado.getEmail());
                edt_senha.setText(usuarioSelecionado.getSenha());
                Toast.makeText(GerenciaUsuariosActivity.this, usuarioSelecionado.getUid() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**INICIALIZA A CONEX??O COM FIREBASE AUTH LOGO NO START DA ACTIVITY**/
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = Conexao.getFirebaseAuth();
    }
    /**CONTROLA A A????O DO BOT??O (OU GESTO) VOLTAR**/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    /**CRIA O MENU DA ACTION BAR**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crud, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /**CONTROLA OS EVENTOS DOS BOT??ES DA ACTION BAR**/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_salvar:
                String email = edt_email.getText().toString().trim();
                String senha = edt_senha.getText().toString().trim();
                String nome = edt_nome.getText().toString().trim();

                if (email.isEmpty() || senha.isEmpty() || nome.isEmpty()) {
                    alert("Preencha os campos ou selecione um usu??rio para editar.");
                } else {
                    if (usuarioSelecionado == null) { //CREATE
                        //inicia a cria????o de um usu??rio na autentica????o do firebase
                        criarUsuarioAuth(email, nome, senha);

                    } else { //UPDATE
                        Usuario usuario = new Usuario();
                        usuario.setUid(usuarioSelecionado.getUid());
                        usuario.setNome(edt_nome.getText().toString().trim());
                        usuario.setEmail(edt_email.getText().toString().trim());
                        usuario.setSenha(edt_senha.getText().toString().trim());
                        databaseReference.child(Global.no_usuario).child(usuario.getUid()).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(GerenciaUsuariosActivity.this, "Usu??rio atualizado com sucesso!", Toast.LENGTH_LONG).show();
                                    limparCampos();
                                } else {
                                    Log.e("FALHA BD >>>>" , task.getException().toString());
                                    Toast.makeText(GerenciaUsuariosActivity.this, "Falha ao atualizar o usu??rio", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                break;

            case R.id.menu_excluir:
                if (edt_email.getText().toString().isEmpty()) {
                    alert("N??o h?? nada pra apagar");
                } else {
                    new AlertDialog.Builder(GerenciaUsuariosActivity.this)
                            .setTitle("O usu??rio " + edt_nome.getText() + " ser?? exclu??do.")
                            .setMessage("Deseja realmente excluir o usu??rio?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Usuario uDelete = new Usuario();
                                    uDelete.setUid(usuarioSelecionado.getUid());
                                    databaseReference.child(Global.no_usuario).child(uDelete.getUid()).removeValue();
                                    alert("Usu??rio exclu??do com sucesso.");
                                    limparCampos();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                break;

            case R.id.menu_sair:
                Intent i = new Intent(GerenciaUsuariosActivity.this, ActivityHome.class);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    /**INICIALIZA A CONEX??O COM A DATABASE**/
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(GerenciaUsuariosActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }
    /**MONITORA TODAS AS ALTERA????ES SOFRIDAS PELA DATABASE**/
    private void eventoDatabase() {
        databaseReference.child(Global.no_usuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsuarios.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Usuario u = objSnapshot.getValue(Usuario.class);
                    listUsuarios.add(u);
                }
                arrayAdapterUsuario = new ArrayAdapter<Usuario>(GerenciaUsuariosActivity.this, android.R.layout.simple_list_item_1, listUsuarios);
                list_usuarios.setAdapter(arrayAdapterUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /**CRIA USU??RIO NO SERVI??O DE AUTENTICA????O DO FIREBASE**/
    private void criarUsuarioAuth(String email, String nome, String senha) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    //inicia a persistencia do usu??rio na base de dados
                    criarUsuarioBD(email, senha, nome, uid);
                } else {
                    Toast.makeText(GerenciaUsuariosActivity.this, "Falha ao criar autentica????o para o usu??rio", Toast.LENGTH_LONG).show();
                    Log.e("FALHA AUTH >>>>" , task.getException().toString());
                }
            }
        });
    }
    /**CRIA O USU??RIO NA BASE DE DADOS, LOGO AP??S SER CRIADO NA CAMADA DE AUTENTICA????O**/
    private void criarUsuarioBD(String email, String senha, String nome, String uid) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUid(uid);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);
        novoUsuario.setNome(nome);
        databaseReference.child(Global.no_usuario).child(uid).setValue(novoUsuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(GerenciaUsuariosActivity.this, "Usu??rio criado com sucesso!", Toast.LENGTH_LONG).show();
                    limparCampos();
                } else {
                    Toast.makeText(GerenciaUsuariosActivity.this, "Falha ao cadastrar o usu??rio na base de dados!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /**CRIA UM ALERTA DO TIPO TOAST**/
    private void alert(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
    /**LIMPA TODOS SO CAMPOS DA TELA E REINICIA A VARI??VEL "usuarioSelecionado"**/
    private void limparCampos() {
        edt_nome.setText("");
        edt_email.setText("");
        edt_senha.setText("");
        usuarioSelecionado = null;
    }

    /**A FAZERES NESSA CLASSE**/
    //TODO CORRIGIR O PROBLEMA PARA QUANDO O USU??RIO SELECIONA UM USU??RIO E EM SEGUIDA APAGA OS CAMPOS...
    //TODO ... NESSA SITUA????O, O USU??RIO AO INV??S DE CRIAR UM NOVO PERFIL, ACABA EDITANDO O ULTIMO USUARIO QUE FOI SELECIONADO.

    //TODO MELHORAR O LAYOUT, AS CORES E OS ICONES - LEONAN
}