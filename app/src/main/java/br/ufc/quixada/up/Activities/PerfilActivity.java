package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import br.ufc.quixada.up.R;

import br.ufc.quixada.up.Adapters.PerfilFragmentPagerAdapter;

import static br.ufc.quixada.up.R.id.nav_view;

public class PerfilActivity extends BaseActivity {

    private TabLayout perfilTabLayout;
    private ViewPager perfilViewPager;

    public String profilePictureName;

//    FirebaseStorage storage = FirebaseStorage.getInstance();
//    public StorageReference storageRef;
//    public StorageReference pathReference;

//    public static byte[] image;
    public static String nome;
    public static String endereco;
    public static Map<String, String> enderecoMap = new HashMap<String, String>();
    String[] keyValuePairs;
    public static String id;
    public static String email;
    public static String fotoPerfil;
//    TextView textViewEmail;
//    TextView textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        perfilTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        perfilViewPager = (ViewPager) findViewById(R.id.view_pager_perfil);

        perfilViewPager.setAdapter(new PerfilFragmentPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.tabs_perfil)));

        perfilTabLayout.setupWithViewPager(perfilViewPager);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if(user != null){
            updateUserInfo();
        }

        nome = firebasePreferences.getUserName();
        endereco = firebasePreferences.getAdress();

        endereco = endereco.substring(1, endereco.length()-1);
        keyValuePairs = endereco.split(",");
        for (String pair : keyValuePairs){
            String[] entry = pair.split("=");
            enderecoMap.put(entry[0].trim(), entry[1].trim());
        }

        id = firebasePreferences.getId();
        email = firebasePreferences.getUserEmail();
        fotoPerfil = profilePictureName;

//        Toast.makeText(this,"foto do local user ao criar ativity: "+fotoPerfil, Toast.LENGTH_LONG).show();


//        Log.d("FOTO PERFIL TESTE", fotoPerfil);

    }

    @Override
    public void onResume(){
        super.onResume();
        fotoPerfil = localUser.getFotoPerfil();
        localUser.setFotoPerfil(fotoPerfil);
//        Toast.makeText(this,"foto do local user on resume activity "+fotoPerfil, Toast.LENGTH_LONG).show();

//        Log.d("FOTO PERFIL", fotoPerfil);
//        fotoPerfil = firebasePreferences.getProfilePicture();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        fotoPerfil = localUser.getFotoPerfil();
        localUser.setFotoPerfil(fotoPerfil);
//        Toast.makeText(this,"foto do local user on pause activity "+fotoPerfil, Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_edit:
                Intent intent = new Intent(this, EditPerfilActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
