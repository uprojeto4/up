package br.ufc.quixada.up;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import br.ufc.quixada.up.Activities.MainActivity;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.Utils.Base64Custom;
import br.ufc.quixada.up.Utils.FirebasePreferences;

public class CadastroActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;
    private Button buttonSignup;
    private User user;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editTextName = findViewById(R.id.nameInput);
        editTextEmail = findViewById(R.id.emailInput);
        editTextPassword = findViewById(R.id.passwordInput);
        editTextPasswordConfirm = findViewById(R.id.passwordConfirmationInput);
        buttonSignup = findViewById(R.id.buttonSignup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !editTextName.getText().toString().equals("") &&
                    !editTextEmail.getText().toString().equals("") &&
                    !editTextPassword.getText().toString().equals("") &&
                    !editTextPasswordConfirm.getText().toString().equals("")){

                    if (editTextPassword.getText().toString().equals(editTextPasswordConfirm.getText().toString())) {
                        user = new User();
                        user.setNome(editTextName.getText().toString());
                        user.setEmail(editTextEmail.getText().toString());
                        user.setSenha(editTextPassword.getText().toString());
                    } else {
                        Toast.makeText(CadastroActivity.this, "As senhas não são correspondentes", Toast.LENGTH_LONG).show();
                    }
                    cadastrarUsuario();
                }else{
                    Toast.makeText(getBaseContext(), "Por favor preencha os campos vazios!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void cadastrarUsuario() {
        auth = FirebaseConfig.getAuth();
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getSenha())
                .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                           Toast.makeText(CadastroActivity.this, "Usuário cadastrado", Toast.LENGTH_LONG).show();

                           String userId = Base64Custom.encodeBase64(user.getEmail());
                           FirebaseUser firebaseUser = task.getResult().getUser();
                           user.setId(userId);
                           user.save();

                           FirebasePreferences preferences = new FirebasePreferences(CadastroActivity.this);
                           preferences.SaveUserPreferences(userId, user.getNome());

                           Intent intent = new Intent(getBaseContext(), MainActivity.class);
                           startActivity(intent);
                       } else{
                           String erro = "";

                           try{
                               throw task.getException();
                           } catch (FirebaseAuthWeakPasswordException e){
                                erro = "Sua senha está fraca, digite uma senha de no mínimo 8 caracteres";
                           } catch (FirebaseAuthInvalidCredentialsException e){
                               erro = "Email inválido";
                           } catch (FirebaseAuthUserCollisionException e){
                               erro = "Esse email já está cadastrado";
                           }catch (Exception e){
                               erro = "Falha ao cadastrar";
                               e.printStackTrace();
                           }
                           Toast.makeText(CadastroActivity.this, "Erro: " + erro, Toast.LENGTH_LONG).show();

                       }
                    }
                });
    }
}
