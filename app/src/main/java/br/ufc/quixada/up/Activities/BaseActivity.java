package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.FirebasePreferences;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebasePreferences firebasePreferences;
    DatabaseReference databaseReference;
    TextView textViewName;
    TextView textViewEmail;
    ImageView imageViewPhoto;
    User localUser;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    StorageReference profilePictureRef;

    public byte[] image;

    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseConfig.getAuth();
        user = auth.getCurrentUser();
        databaseReference = FirebaseConfig.getDatabase();
        localUser = User.getInstance();
        firebasePreferences = new FirebasePreferences(BaseActivity.this);

        localUser.setId(firebasePreferences.getId());
        localUser.setNome(firebasePreferences.getUserName());
        localUser.setEmail(firebasePreferences.getUserEmail());
        localUser.setFotoPerfil(firebasePreferences.getProfilePicture());
//        localUser.adressToMap(firebasePreferences.getAdress());

//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

//        auth = FirebaseConfig.getAuth();
//        user = auth.getCurrentUser();
//        databaseReference = FirebaseConfig.getDatabase();
//        localUser = new User();
//
//        if(user != null){
//            updateLocalUser();
//        }

    }

//    public void updateLocalUser(){
//
//        Query email = databaseReference.child("users").orderByChild("email").equalTo(user.getEmail());
//        email.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    localUser = singleSnapshot.getValue(User.class);
////                    Toast.makeText(getBaseContext(), "Olá: "+ localUser, Toast.LENGTH_SHORT).show();
////                    textViewName.setText(localUser.getNome());
////                    textViewEmail.setText(localUser.getEmail());
//                    updateProfile();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                Log.e(TAG, "onCancelled", databaseError.toException());
//                Toast.makeText(getBaseContext(), "Usuário não autorizado!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
////        ValueEventListener userListener = new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                // Get Post object and use the values to update the UI
//////                String s = dataSnapshot.child("users").child("01").getValue(String.class);
////                User user = dataSnapshot.child("user").child("aXNhYWMtcGpAaG90bWFpbC5jb20=").getValue(User.class);
////                Toast.makeText(getBaseContext(), "Opa: " + user, Toast.LENGTH_LONG).show();
////                // ...
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                // Getting Post failed, log a message
////                Toast.makeText(getBaseContext(), "Opa, deu merda!", Toast.LENGTH_LONG).show();
////                // ...
////            }
////        };
//        //Executa sempre que os dados mudarem
////        databaseReference.addValueEventListener(userListener);
//
//        //Executa apenas uma vez
////        databaseReference.addListenerForSingleValueEvent(userListener);
//
//    }
//
//
//    public void updateProfile(){
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName(localUser.getNome())
//                .build();
//
//        user = auth.getCurrentUser();
//
//        if(user != null){
//            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    Toast.makeText(getBaseContext(), "Olá "+ user.getDisplayName() +"! :)", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Primeiro testa se o drawer existe, pois estava obtendo erro ao pressionar o back na editPerfilActivity, pois ela não possuía drawer
        //e ainda assim precisava herdar dela para obter os dados do usuário
        if(drawer == null){
            super.onBackPressed();
        }else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_anuncio) {
            // Handle the camera action
            Intent intent = new Intent(this, NovoAnuncioActivity.class);
//            isso serve para usar flags de configurações do intent

//            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_negociacoes) {
            Intent intent = new Intent(this, NegociacoesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_lista_desejos) {
            Intent intent = new Intent(this, ListaDesejosActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categorias) {
            Intent intent = new Intent(this, CategoriasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_perfil) {
            Intent intent = new Intent(this, PerfilActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_configuracoes) {
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sair) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    //Atualizar usuario local
//    public void updateLocalUser(){
//
//        Query email = databaseReference.child("users").orderByChild("email").equalTo(user.getEmail());
//        email.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    localUser = singleSnapshot.getValue(User.class);
////                    Toast.makeText(getBaseContext(), "Olá: "+ localUser, Toast.LENGTH_SHORT).show();
//                    updateProfile();
//                    updateUserInfo();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                Log.e(TAG, "onCancelled", databaseError.toException());
//                Toast.makeText(getBaseContext(), "Usuário não autorizado!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
////        ValueEventListener userListener = new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                // Get Post object and use the values to update the UI
//////                String s = dataSnapshot.child("users").child("01").getValue(String.class);
////                User user = dataSnapshot.child("user").child("aXNhYWMtcGpAaG90bWFpbC5jb20=").getValue(User.class);
////                Toast.makeText(getBaseContext(), "Opa: " + user, Toast.LENGTH_LONG).show();
////                // ...
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                // Getting Post failed, log a message
////                Toast.makeText(getBaseContext(), "Opa, deu merda!", Toast.LENGTH_LONG).show();
////                // ...
////            }
////        };
//        //Executa sempre que os dados mudarem
////        databaseReference.addValueEventListener(userListener);
//
//        //Executa apenas uma vez
////        databaseReference.addListenerForSingleValueEvent(userListener);
//
//    }

    //Metodo pode ser chamado a partir de qualquer activity que extend de BaseAvtivity
    public void updateUserInfo(){

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nav_view =  navigationView.getHeaderView(0);

        textViewName = (TextView)nav_view.findViewById(R.id.textViewName);
        textViewEmail = (TextView)nav_view.findViewById(R.id.textViewEmail);
        imageViewPhoto = (ImageView)nav_view.findViewById(R.id.imageView);

        profilePictureRef = storage.getReference().child("UsersProfilePictures/"+localUser.getId()+"/"+localUser.getFotoPerfil());
//        Toast.makeText(getBaseContext(),profilePictureRef.getPath(), Toast.LENGTH_LONG).show();


        downloadProfilePicture();

        textViewName.setText(localUser.getNome());
        textViewEmail.setText(localUser.getEmail());
    }

    public void downloadProfilePicture(){
        //o download com o metodo getFile deve ser feito num try/catch
        try{
            //cria o arquivo temporário local onde a imagem será armazenada
            final File localFile = File.createTempFile("jpg", "image");
            profilePictureRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                //monitora o sucesso do download
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //transforma a imagem baixada em um bitmap
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    //transforma o bitmap em stream
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //transforma o stream em um array de bytes
                    image = stream.toByteArray();
                    //método que aplica a imagem nos lugares desejsdos
                    applyImage(image);
//                    Toast.makeText(getActivity(),profilePictureRef.getName(), Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                //monitora a falha do downlaod
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getBaseContext(),"Foto não encontrada", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e){
            e.printStackTrace();
            //manipular exceções
            Log.e("Main", "IOE exception");
        }

    }

    public void applyImage(byte[] bytes){
        //efeito de blur para a imagem
        MultiTransformation multi = new MultiTransformation(
                new BlurTransformation(25));


        //options para o glide
        RequestOptions requestOptions = new RequestOptions();
        //não salava a imagem em cache, para que ela possa ser alterada caso outra pessoa se logue
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);

//        //carrega a imagem
//        Glide.with(this).load(bytes)
//                //aplica as options de cache
//                .apply(requestOptions)
//                //aplica as options de transformação
//                .apply(RequestOptions.bitmapTransform(multi))
//                //insere a imagem no imageView
//                .into((ImageView) findViewById((R.id.header_cover_image)));

        Glide.with(this).load(bytes)
                .apply(requestOptions)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(200)))
                .into(imageViewPhoto);
    }

    //Desloga o usuario da aplicação
    public void signOut(){
        auth = FirebaseConfig.getAuth();
        auth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}