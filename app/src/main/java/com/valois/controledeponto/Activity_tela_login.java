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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.valois.controledeponto.Utils.Conexao;

public class Activity_tela_login extends AppCompatActivity {

    FirebaseAuth auth;
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
            auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(Activity_tela_login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(Activity_tela_login.this, GerenciaUsuariosActivity.class);
                        startActivity(i);
                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        alert("Erro ao cadastrar o usuário");
                    }
                }
            });
        }
    }

    private void alert(String s) {
        Toast.makeText(Activity_tela_login.this,s,Toast.LENGTH_LONG).show();
    }
}