package br.ufc.quixada.up;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.ufc.quixada.up.Activities.MainActivity;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.User;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText senha;
    User usuarios;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.editText_email);
        senha = (EditText) findViewById(R.id.editText_senha);

    }


    public void fazerLogin(View view){

        if(!email.getText().toString().equals("") && !senha.getText().toString().equals("")){

            usuarios = new User();
            usuarios.setEmail(email.getText().toString());
            usuarios.setSenha(senha.getText().toString());
            validarLogin();

        }else{
            Toast.makeText(this, "Email ou Senha não preenchidos!", Toast.LENGTH_SHORT).show();
        }

    }

    public void fazerCadastro(View view){
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    private void validarLogin(){
                Log.d("tag", "testetetetete");
        auth = FirebaseConfig.getAuth();
        auth.signInWithEmailAndPassword(usuarios.getEmail(), usuarios.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getBaseContext(), "Olá "+ usuarios.getNome() +"! :)", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(), "Sinto muito :( email ou senha incorretos!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
