package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseConfig.getAuth();
        user = auth.getCurrentUser();

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run(){
                //teste de user Logged
                if(user != null){
                    openHome();
                }else {
                    openLogin();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void openLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openHome(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
