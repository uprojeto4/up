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
    User usuario;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.editText_email);
        senha = (EditText) findViewById(R.id.editText_senha);



        //Inicializando variaveis
        databaseReference = FirebaseConfig.getDatabase();
        auth = FirebaseConfig.getAuth();
        user = auth.getCurrentUser();

//        updateLocalUser();
    }

    public void fazerLogin(View view){

        if(!email.getText().toString().equals("") && !senha.getText().toString().equals("")){
            usuario = new User();
            usuario.setEmail(email.getText().toString());
            usuario.setSenha(senha.getText().toString());
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
        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    openHome();
                    updateProfile();
//                    Toast.makeText(getBaseContext(), "Olá "+ user.getDisplayName() +"! :)", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(), "Sinto muito :( email ou senha incorretos!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void openHome(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void updateProfile(){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .build();

        user = auth.getCurrentUser();

        if(user != null){
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getBaseContext(), "Olá "+ user.getDisplayName() +"! :)", Toast.LENGTH_LONG).show();
                    updateLocalUser();
                }
            });
        }
    }

    public void updateLocalUser(){

//        DatabaseReference firabaseUser = databaseReference.child("user").child("Y2FtaWxsYUBnbWFpbC5jb20=").child("nome");

//        Toast.makeText(getBaseContext(), "Opa", Toast.LENGTH_SHORT).show();

//        ValueEventListener userListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//                String user = dataSnapshot.child("user").child("Y2FtaWxsYUBnbWFpbC5jb20=").child("nome").getValue(String.class);
//                Toast.makeText(getBaseContext(), "Opa" + user, Toast.LENGTH_SHORT).show();
//                // ...
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
////                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//        databaseReference.addValueEventListener(userListener);

//        Query email = databaseReference.orderByChild("email").equalTo("isaac-pj@hotmail.com");
//        email.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    User u = singleSnapshot.getValue(User.class);
//                    Toast.makeText(getBaseContext(), "Opa lele" + user.toString(), Toast.LENGTH_LONG).show();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                Log.e(TAG, "onCancelled", databaseError.toException());
//                Toast.makeText(getBaseContext(), "Opa deu merda!", Toast.LENGTH_LONG).show();
//            }
//        });

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.child("user").child("aXNhYWMtcGpAaG90bWFpbC5jb20=").getValue(User.class);
                Toast.makeText(getBaseContext(), "Opa" + user.getNome(), Toast.LENGTH_LONG).show();
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getBaseContext(), "Opa deu merda!", Toast.LENGTH_LONG).show();
                // ...
            }
        };
        databaseReference.addListenerForSingleValueEvent(userListener);

    }
}
