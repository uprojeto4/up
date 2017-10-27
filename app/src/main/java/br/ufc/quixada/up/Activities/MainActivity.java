package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.like.LikeButton;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Adapters.PostAdapter;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;

public class MainActivity extends BaseActivity{

    ArrayList<Post> posts = new ArrayList<Post>();
    Post post = new Post();
    Post post2 = new Post();
    Post post3 = new Post();
  
    public static User localUser;
    TextView textViewEmail;
    TextView textViewName;

    LikeButton likeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNovoAnuncio);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NovoAnuncioActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_view =  navigationView.getHeaderView(0);

//       #Essas variaveis foram inicializadas na BaseActivity
//        auth = FirebaseConfig.getAuth();
//        user = auth.getCurrentUser();
//        databaseReference = FirebaseConfig.getDatabase();
//        localUser = User.getInstance();

        if(user != null){
            updateLocalUser();
        }


        likeButton = (LikeButton) findViewById(R.id.heart_button);


//        for (int i = 0; i<5; i++){
//            Post post = new Post();
//            post.setTitle("Meu Post de num "+(i + 1));
//            post.setSubtitle("Esse post é massa d+ "+(i + 1));
//            post.setPrice(12.00);
//
//            posts.add(post);
//        }




        post.setTitle("Pão fresquinho");
        post.setSubtitle("Pense num pão bom, mais é bom, é bom mesmo!");
        post.setPrice(12.99);


        post2.setTitle("Bicicleta Caloi 100");
        post2.setSubtitle("Bike semi nova, 3 meses de uso, perfeito estado, ótimo preço");
        post2.setPrice(469.99);


        post3.setTitle("Sapato salto Vizano");
        post3.setSubtitle("Sapato em ótimo estado, apenas uns 7 anos de uso, cor de carnaval, muito confortável, tipo uma pedra");
        post3.setPrice(59.99);

        posts.add(post);
        posts.add(post2);
        posts.add(post3);


        ListView listView = (ListView)findViewById(R.id.lv_cards);
        listView.setAdapter(new PostAdapter(this, posts));

        listView.setOnItemClickListener(anuncioTela());

    }

    public AdapterView.OnItemClickListener anuncioTela() {
        return (new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {

//                Toast.makeText(getBaseContext(),"Clicked item", Toast.LENGTH_LONG).show();
//                Log.d("debug","teste");

                Intent intent = new Intent(getBaseContext(), AnuncioActivity.class);
                startActivity(intent);
            }
        });
    }

    public void share(View view){

//        Uri imageUri1 = R.drawable.image_test_1;

        TextView textView_title = (TextView)findViewById(R.id.textView_title);
        TextView textView_describ = (TextView)findViewById(R.id.textView_describ);
        TextView textView_price = (TextView)findViewById(R.id.textView_price);


        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textView_title.getText() +
                            " - " + textView_describ.getText() +
                            " - " + textView_price.getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

//        ArrayList<Uri> imageUris = new ArrayList<Uri>();
//        imageUris.add(imageUri1); // Add your image URIs here
//        imageUris.add(imageUri2);
//
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
//        shareIntent.setType("image/*");
//        startActivity(Intent.createChooser(shareIntent, "Share images to.."));
    }

    public void up(View view){
        Toast.makeText(getBaseContext(),"Dar um up maroto", Toast.LENGTH_SHORT).show();
    }

    public void negociar(View view){
        Toast.makeText(getBaseContext(),"Abrir tela de chat", Toast.LENGTH_SHORT).show();
    }

//    //Atualizar usuario local
//    public void updateLocalUser(){
//
//        Query email = databaseReference.child("users").orderByChild("email").equalTo(user.getEmail());
//        email.addListenerForSingleValueEvent(new ValueEventListener() {

    public void favorite(View view) {
//        favorite = (ImageButton) findViewById(R.id.favorite);
//        favorite.setColorFilter(Color.argb(255, 68, 68, 68));
        likeButton.setLiked(true);
//        Toast.makeText(getBaseContext(),"Abrir tela de chat", Toast.LENGTH_SHORT).show();
    }
    //Atualizar usuario local
    public void updateLocalUser(){

        Query email = databaseReference.child("users").orderByChild("email").equalTo(user.getEmail());
        email.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    localUser = singleSnapshot.getValue(User.class);
//                    Toast.makeText(getBaseContext(), "Olá: "+ localUser, Toast.LENGTH_SHORT).show();
                    textViewName.setText(localUser.getNome());
                    textViewEmail.setText(localUser.getEmail());
                    updateProfile();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled", databaseError.toException());
                Toast.makeText(getBaseContext(), "Usuário não autorizado!", Toast.LENGTH_SHORT).show();
            }
        });

//        ValueEventListener userListener = new ValueEventListener() {
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
//
//    //Atualizar propriedades do objeto currentUser do firebase
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
}
