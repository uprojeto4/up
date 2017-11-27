package br.ufc.quixada.up.Activities;

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
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.Base64Custom;
import br.ufc.quixada.up.Utils.FirebasePreferences;

public class CadastroActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;

    private EditText editTextLogradouro;
    private EditText editTextNumero;
    private EditText editTextComplemento;
    private EditText editTextBairro;
    private EditText editTextCidade;
    private EditText editTextEstado;

    private Button buttonSignup;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private User localUser;
    Map endereco = new HashMap<String, String>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editTextName = findViewById(R.id.nameInput);
        editTextEmail = findViewById(R.id.emailInput);
        editTextPassword = findViewById(R.id.passwordInput);
        editTextPasswordConfirm = findViewById(R.id.passwordConfirmationInput);

        editTextLogradouro = findViewById(R.id.logradouroInput);
        editTextNumero = findViewById(R.id.numeroInput);
        editTextComplemento = findViewById(R.id.complementoInput);
        editTextBairro = findViewById(R.id.bairroInput);
        editTextCidade = findViewById(R.id.cidadeInput);
        editTextEstado = findViewById(R.id.estadoInput);

        buttonSignup = findViewById(R.id.buttonSignup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !editTextName.getText().toString().equals("") &&
                    !editTextEmail.getText().toString().equals("") &&
                    !editTextPassword.getText().toString().equals("") &&
                    !editTextPasswordConfirm.getText().toString().equals("")){

                    if (editTextPassword.getText().toString().equals(editTextPasswordConfirm.getText().toString())) {
                        localUser = User.getInstance();
                        localUser.setNome(editTextName.getText().toString());
                        localUser.setEmail(editTextEmail.getText().toString());

//                        localUser.setLogradouro(editTextLogradouro.getText().toString());
//                        localUser.setNumero(editTextNumero.getText().toString());
//                        localUser.setComplemento(editTextComplemento.getText().toString());
//                        localUser.setBairro(editTextBairro.getText().toString());
//                        localUser.setCidade(editTextCidade.getText().toString());
//                        localUser.setEstado(editTextEstado.getText().toString());
//                        Toast.makeText(CadastroActivity.this, editTextLogradouro.getText().toString(), Toast.LENGTH_SHORT).show();

                        adressToMap(editTextLogradouro.getText().toString(),
                                editTextNumero.getText().toString(),
                                editTextComplemento.getText().toString(),
                                editTextBairro.getText().toString(),
                                editTextCidade.getText().toString(),
                                editTextEstado.getText().toString());

                        localUser.setFotoPerfil("profile_picture_default.jpg");
                    } else {
                        Toast.makeText(CadastroActivity.this, "As senhas não são correspondentes", Toast.LENGTH_SHORT).show();
                    }
                    cadastrarUsuario();
                }else{
                    Toast.makeText(getBaseContext(), "Por favor preencha os campos vazios!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //Inicializando variaveis
        databaseReference = FirebaseConfig.getDatabase();
        auth = FirebaseConfig.getAuth();
        user = auth.getCurrentUser();
    }

    public void adressToMap(String logradouro, String numero, String complemento, String bairro, String cidade, String estado){
        this.endereco.put("logradouro", new String(logradouro));
        this.endereco.put("numero", new String(numero));
        this.endereco.put("complemento", new String(complemento));
        this.endereco.put("bairro", new String(bairro));
        this.endereco.put("cidade", new String(cidade));
        this.endereco.put("estado", new String(estado));
        localUser.setEndereco(endereco);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        finish();
//    }

    private void cadastrarUsuario() {
        auth = FirebaseConfig.getAuth();
        auth.createUserWithEmailAndPassword(localUser.getEmail(), editTextPassword.getText().toString())
            .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                   if (task.isSuccessful()) {
//                       Toast.makeText(CadastroActivity.this, "Usuário cadastrado", Toast.LENGTH_LONG).show();
                       String userId = Base64Custom.encodeBase64(localUser.getEmail());
                       FirebaseUser firebaseUser = task.getResult().getUser();
                       localUser.setId(userId);
                       localUser.save();

                       FirebasePreferences preferences = new FirebasePreferences(CadastroActivity.this);
                       preferences.SaveUserPreferences(userId, localUser.getNome(), localUser.getEmail(), localUser.getFotoPerfil(), localUser.getEndereco());

                       Intent intent = new Intent(getBaseContext(), MainActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(intent);
                       Toast.makeText(getBaseContext(), "Bem Vindo, "+ localUser.getNome() +"! :)", Toast.LENGTH_LONG).show();
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
                       Toast.makeText(CadastroActivity.this, "Erro: " + erro, Toast.LENGTH_SHORT).show();
                   }
                }
            });
    }
}
