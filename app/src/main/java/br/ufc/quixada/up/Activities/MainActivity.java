package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import br.ufc.quixada.up.Utils.FirebasePreferences;

public class MainActivity extends BaseActivity{

    ArrayList<Post> posts = new ArrayList<Post>();
    private RecyclerView recyclerView;
    Post post = new Post();
    Post post2 = new Post();
    Post post3 = new Post();

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


        if(user != null){
            updateUserInfo();
        }

//        firebasePreferences = new FirebasePreferences(MainActivity.this);
//        Toast.makeText(this, firebasePreferences.getId()+" - "+firebasePreferences.getUserName()+" - "+firebasePreferences.getUserEmail(), Toast.LENGTH_LONG).show();

        likeButton = (LikeButton) findViewById(R.id.heart_button);

        //RecycleView Implementation
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewPosts);
        recyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


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


        PostAdapter postAdapter = new PostAdapter(this, posts);
        recyclerView.setAdapter(postAdapter);


//        ListView listView = (ListView)findViewById(R.id.lv_cards);
//        listView.setAdapter(new PostAdapter(this, posts));

//        listView.setOnItemClickListener(anuncioTela());

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

    public void favorite(View view) {
//        favorite = (ImageButton) findViewById(R.id.favorite);
//        favorite.setColorFilter(Color.argb(255, 68, 68, 68));
        likeButton.setLiked(true);
//        Toast.makeText(getBaseContext(),"Abrir tela de chat", Toast.LENGTH_SHORT).show();
    }
}
