package com.valois.controledeponto;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valois.controledeponto.utils.Conexao;
import com.valois.controledeponto.utils.Global;

public class ActivityLogin extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String no_principal = "Usuario";

    EditText edt_email, edt_senha;
    Button btn_entrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);

        //inicializa todos os componentes da view
        inicilizarComponentes();
        //responsável pelos eventos de click dos botões
        eventos();
        //Inicializa a base de dados no firebase
        inicializarFirebaseBD();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Conexao.getFirebaseAuth();
    }

    private void inicilizarComponentes() {
        btn_entrar = findViewById(R.id.btn_login_entrar);
        edt_email = findViewById(R.id.edt_login_email);
        edt_senha = findViewById(R.id.edt_login_senha);
        edt_email.setText("lleandrovalois@gmail.com");
        edt_senha.setText("161002");
    }

    private void eventos() {
        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticaUsuario();
            }
        });
    }

    private void autenticaUsuario() {
        if (edt_email.getText().toString().isEmpty()) {
            alert("Informe o seu e-mail!");
        } else if (edt_senha.getText().toString().isEmpty()) {
            alert("Informe sua senha!");
        } else {
            String email = edt_email.getText().toString().trim();
            String senha = edt_senha.getText().toString().trim();
            auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        carregarUsuarioExistente(auth.getCurrentUser());
                        Intent i = new Intent(ActivityLogin.this, ActivityHome.class);
                        startActivity(i);
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        alert("Erro ao cadastrar o usuário");
                    }
                }
            });
        }
    }

    private void carregarUsuarioExistente(FirebaseUser user){
        Global.uid_usuario = user.getUid();
    }

    private void inicializarFirebaseBD() {
        FirebaseApp.initializeApp(ActivityLogin.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //TODO ESTUDAR A DOCUMENTAÇÃO DESSA FUNCIONALIDADE
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

    }

    private void alert(String s) {
        Toast.makeText(ActivityLogin.this,s,Toast.LENGTH_LONG).show();
    }
}