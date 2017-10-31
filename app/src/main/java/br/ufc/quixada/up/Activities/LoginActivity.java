package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText senha;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private User usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Recuperando Componentes
        email = (EditText) findViewById(R.id.editText_email);
        senha = (EditText) findViewById(R.id.editText_senha);

        //Inicializando variaveis
        databaseReference = FirebaseConfig.getDatabase();
        auth = FirebaseConfig.getAuth();
        user = auth.getCurrentUser();
    }

    //Abrir tela de Cadastro
    public void fazerCadastro(View view){
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    //Abrir tela Home
    private void openHome(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    //Metodo para fazer o login
    public void fazerLogin(View view){
        if(!email.getText().toString().equals("") && !senha.getText().toString().equals("")){
            usuario = User.getInstance();
            usuario.setEmail(email.getText().toString());
            validarLogin();
        }else{
            Toast.makeText(this, "Email ou Senha não preenchidos!", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para autenticar ususario com email e senha
    private void validarLogin(){
        auth.signInWithEmailAndPassword(usuario.getEmail(), senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    openHome();
                }else{
                    Toast.makeText(getBaseContext(), "Sinto muito :( email ou senha incorretos!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
