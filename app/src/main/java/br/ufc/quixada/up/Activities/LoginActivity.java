package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Address;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.FirebasePreferences;

//import static br.ufc.quixada.up.Fragments.fragmentPerfilPerfil.getFacebookProfilePicture;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText senha;

    Button skipLogin;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebasePreferences firebasePreferences;
    private DatabaseReference databaseReference;
    private User localUser;
    private Address localAddress;

    LoginButton loginButton;
    CallbackManager mCallbackManager;

    public static String facebookUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        //Recuperando Componentes
        email = (EditText) findViewById(R.id.editText_email);
        senha = (EditText) findViewById(R.id.editText_senha);

        //Inicializando variaveis
        databaseReference = FirebaseConfig.getDatabase();
        auth = FirebaseConfig.getAuth();

        loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
    }

    public void facebookLogin(View view){
        mCallbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult.getAccessToken());
//                facebookUserId = loginResult.getAccessToken()+"";
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TAG", "facebook:onError", error);
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token){
        Log.d("TAG", "handleFacebookAccessToken:" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d("TAG", "signInWithCredential:success");
                    user = auth.getCurrentUser();
                    MainActivity.isLogged = true;
                    Log.d("TESTED", user.getIdToken(true)+"");
                    databaseReference.child("users").orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                User userQ = singleSnapshot.getValue(User.class);
                                if (userQ.getId().equals(user.getUid())){
//                                    facebookUserId = user.getUid();
                                    Log.d("TAGFACE", user.getUid());
//                                    getFacebookProfilePicture(user.getUid());
                                    FirebasePreferences preferences = new FirebasePreferences(LoginActivity.this);
                                    preferences.SaveUserPreferences(userQ.getId(), userQ.getNome(), userQ.getEmail(), userQ.getFotoPerfil(), userQ.getAddress(),
                                            userQ.getNumVendas(), userQ.getAvVendedor(), userQ.getNumCompras(), userQ.getAvComprador());
                                    openHome();
                                }else{
                                    localUser = new User();
                                    Log.d("TAGFACE", user.getUid());
//                                    facebookUserId = user.getUid();
                                    localUser.setId(user.getUid());
                                    localUser.setEmail(user.getEmail());
                                    localUser.setNome(user.getDisplayName());
                                    localUser.save();
                                    FirebasePreferences preferences = new FirebasePreferences(LoginActivity.this);
                                    preferences.SaveUserPreferences(user.getUid(), localUser.getNome(), localUser.getEmail(), localUser.getFotoPerfil(), localUser.getAddress(),
                                            localUser.getNumVendas(), localUser.getAvVendedor(), localUser.getNumCompras(), localUser.getAvComprador());
                                    openHome();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//                    localUser = new User();
//                    Log.d("TAGFACE", user.getUid());
//                    localUser.setId(user.getUid());
//                    localUser.setEmail(user.getEmail());
//                    localUser.setNome(user.getDisplayName());
//                    localUser.save();
//                    FirebasePreferences preferences = new FirebasePreferences(LoginActivity.this);
//                    preferences.SaveUserPreferences(user.getUid(), localUser.getNome(), localUser.getEmail(), localUser.getFotoPerfil(), localUser.getAddress(),
//                            localUser.getNumVendas(), localUser.getAvVendedor(), localUser.getNumCompras(), localUser.getAvComprador());
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    updateUI(user);
//                    openHome();
                }else{
                    Log.w("TAG", "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


//    Bitmap bitmap = getFacebookProfilePicture(userId);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void skipLogin(View view){
//        else {
        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    user = auth.getCurrentUser();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
                    openHome();
//                    updateProfile();
                } else{
                    Log.d("brendon", "caiu no else");
                }
            }
        });
//        }
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
//                    GenericTypeIndicator<User> usuarios = new GenericTypeIndicator<User>() {};
                    localUser = singleSnapshot.getValue(User.class);
                    MainActivity.isLogged = true;
//                    Address localAddress = singleSnapshot.child("address").getValue(Address.class);
//                    Toast.makeText(getBaseContext(), "Olá: "+ localUser, Toast.LENGTH_SHORT).show();
//                    Log.d("testeAddress", " " + localAddress);
                }
//                Log.d("testeAddress", " " + localUser.getAddress());
                firebasePreferences = new FirebasePreferences(LoginActivity.this);
                firebasePreferences.SaveUserPreferences(localUser.getId(), localUser.getNome(), localUser.getEmail(), localUser.getFotoPerfil(), localUser.getAddress(),
                        localUser.getNumVendas(), localUser.getAvVendedor(), localUser.getNumCompras(), localUser.getAvComprador());
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
                .build();

//        user = auth.getCurrentUser();

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
