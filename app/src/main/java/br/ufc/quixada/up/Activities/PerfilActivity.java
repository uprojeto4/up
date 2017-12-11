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

import br.ufc.quixada.up.Models.Address;
import br.ufc.quixada.up.R;

import br.ufc.quixada.up.Adapters.PerfilFragmentPagerAdapter;
import br.ufc.quixada.up.Utils.ChatControl;

import static br.ufc.quixada.up.R.id.nav_view;

public class PerfilActivity extends BaseActivity {

    private TabLayout perfilTabLayout;
    public static ViewPager perfilViewPager;

    public String profilePictureName;

    public static String anuncianteId;


//    FirebaseStorage storage = FirebaseStorage.getInstance();
//    public StorageReference storageRef;
//    public StorageReference pathReference;

//    public static byte[] image;
    public static String nome;
    public static Address endereco;
//    public static Map<String, String> enderecoMap = new HashMap<String, String>();
    String[] keyValuePairs;
    public static String id;
    public static String email;
    public static String fotoPerfil;
//    public static String testFoto;


    public static float avComprador;
    public static int numCompras;

    public static float avVendedor;
    public static int numVendas;

    public int fragmentASerAberta;

//    public static String logradouro;
//    public static String numero;
//    public static String complemento;
//    public static String bairro;
//    public static String cidade;
//    public static String estado;

//    TextView textViewEmail;
//    TextView textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        fragmentASerAberta = intent.getIntExtra("fragment", 0);

        Intent intent2 = getIntent();
        anuncianteId = intent2.getStringExtra("idAnunciante");

        Log.d("extra", " "+fragmentASerAberta);

        perfilTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        perfilViewPager = (ViewPager) findViewById(R.id.view_pager_perfil);

        perfilViewPager.setAdapter(new PerfilFragmentPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.tabs_perfil)));
        perfilViewPager.setCurrentItem(fragmentASerAberta);

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

        nome = localUser.getNome();
        endereco = localUser.getAddress();

//        Log.d("endereco_Local+USeer", " "+endereco.getLogradouro());
//        endereco = endereco.substring(1, endereco.length()-1);
//        keyValuePairs = endereco.split(",");
//        for (String pair : keyValuePairs){
//            String[] entry = pair.split("=");
////            enderecoMap.put(entry[0].trim(), entry[1].trim());
//        }

        id = localUser.getId();
        email = localUser.getEmail();
        fotoPerfil = profilePictureName;
//        testFoto = localUser.getFotoPerfil();

        avVendedor = localUser.getAvVendedor();
        numVendas = localUser.getNumVendas();

        avComprador = localUser.getAvComprador();
        numCompras = localUser.getNumCompras();

//        Toast.makeText(this,"foto do local user ao criar ativity: "+fotoPerfil, Toast.LENGTH_LONG).show();


//        Log.d("FOTO PERFIL TESTE", fotoPerfil);


//        for (Map.Entry<String, String> entry : PerfilActivity.enderecoMap.entrySet()){
//            String key = entry.getKey();
//            Log.d("key", " "+ key);
//            if (key.equals("logradouro")){
//                logradouro = entry.getValue();
//                localUser.getAddress().setLogradouro(logradouro);
//            }else if(key.equals("numero")){
//                numero = entry.getValue();
//                localUser.getAddress().setNumero(numero);
//            }else if(key.equals("complemento")){
//                complemento = entry.getValue();
//                localUser.getAddress().setComplemento(complemento);
//            }else if(key.equals("bairro")){
//                bairro = entry.getValue();
//                localUser.getAddress().setBairro(bairro);
//            }else if(key.equals("cidade")){
//                cidade = entry.getValue();
//                localUser.getAddress().setCidade(cidade);
//            }else if(key.equals("estado")){
//                estado = entry.getValue();
//                localUser.getAddress().setEstado(estado);
//            }
//        }

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
//        Intent intent = getIntent();
//        fragmentASerAberta = intent.getIntExtra("fragment", 0);
//        perfilTabLayout.setupWithViewPager(perfilViewPager);


        fotoPerfil = localUser.getFotoPerfil();
        localUser.setFotoPerfil(fotoPerfil);
        nome = localUser.getNome();
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
    protected void onDestroy(){
        super.onDestroy();
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
