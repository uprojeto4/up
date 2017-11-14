package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.FirebasePreferences;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText senha;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebasePreferences firebasePreferences;
    private DatabaseReference databaseReference;
    private User localUser;


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
            validarLogin();
        }else{
            Toast.makeText(this, "Email ou Senha não preenchidos!", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para autenticar ususario com email e senha
    private void validarLogin(){
        auth.signInWithEmailAndPassword(email.getText().toString(), senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = auth.getCurrentUser();
                    getUserByEmail(task.getResult().getUser().getEmail());
                }else{
                    Toast.makeText(getBaseContext(), "Sinto muito :( email ou senha incorretos!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //Atualizar usuario local
    public void getUserByEmail(String e){

        Query email = databaseReference.child("users").orderByChild("email").equalTo(e);
        email.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    localUser = singleSnapshot.getValue(User.class);
//                    Toast.makeText(getBaseContext(), "Olá: "+ localUser, Toast.LENGTH_SHORT).show();
                }
                firebasePreferences = new FirebasePreferences(LoginActivity.this);
                firebasePreferences.SaveUserPreferences(localUser.getId(), localUser.getNome(), localUser.getEmail());
                openHome();
                Toast.makeText(getBaseContext(), "Bem Vindo, "+ localUser.getNome() +"! :)", Toast.LENGTH_LONG).show();
                updateProfile();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled", databaseError.toException());
                Toast.makeText(getBaseContext(), "Usuário não autorizado!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Atualizar propriedades do objeto currentUser do firebase
    public void updateProfile(){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(localUser.getNome())
                .build();

        user = auth.getCurrentUser();

        if(user != null){
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
//              Toast.makeText(getBaseContext(), "Olá "+ user.getDisplayName() +"! :)", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
