package br.ufc.quixada.up.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ufc.quixada.up.Adapters.ListaDesejosPostsAdapter;
import br.ufc.quixada.up.Adapters.TagAdapter;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.Tag;
import br.ufc.quixada.up.R;

import br.ufc.quixada.up.Activities.MainActivity;

public class ListaDesejosActivity extends BaseActivity {

//    private String[] items = {"acessórios", "móveis", "roupas"};
//    TagAdapter tagAdapter = new TagAdapter(this, arrayOfTags);

//    ListView listViewTags;


    RecyclerView myList;

    RecyclerView postsList;

    TagAdapter tagAdapter;

    ListaDesejosPostsAdapter listaDesejosPostsAdapter;

    List<Tag> arrayOfTags = new ArrayList<Tag>();

    List<Post> posts = new ArrayList<Post>();

    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

    LinearLayoutManager postsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

    ImageButton favorite;

    LikeButton likeButton;

    private Spinner spinner;

    String spinnerItem;

    ArrayList<String> filters = new ArrayList<>();

    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_desejos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        likeButton = (LikeButton) findViewById(R.id.heart_button);

        tagAdapter = new TagAdapter(this, arrayOfTags);


//        listViewTags = (ListView) findViewById(R.id.tagListView);
//        listViewTags.setAdapter(tagAdapter);
        myList = (RecyclerView) findViewById(R.id.my_recycler_view);
        myList.setLayoutManager(layoutManager);
        myList.setAdapter(tagAdapter);

        addTags();

        listaDesejosPostsAdapter = new ListaDesejosPostsAdapter(this, posts);
        postsList = (RecyclerView) findViewById(R.id.posts_recycler_view);
        postsList.setLayoutManager(postsLayoutManager);
        postsList.setAdapter(listaDesejosPostsAdapter);
        postsList.setNestedScrollingEnabled(false);

//        updateProfile();

//        TextView nome = (TextView) findViewById(R.id.nome);

//        nome.setText(MainActivity.localUser.getNome());

//        Log.d("TESTE", "nome: "+ MainActivity.localUser.getNome());

        addPosts();


        filters.addAll(Arrays.asList(getResources().getStringArray(R.array.categorias_lista_desejos)));
        spinner = (Spinner) findViewById(R.id.spinnerFilter);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_dropdown_item, filters);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    spinnerItem = (String) adapterView.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(), "Selecionado: " + spinnerItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Opçao Default ", Toast.LENGTH_SHORT).show();
            }
        });

        if(user != null){
            updateUserInfo();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = (MenuItem)navigationView.getMenu().findItem(R.id.nav_lista_desejos);
        menuItem.setChecked(true);
    }


    public void addTags(){
//        ArrayList<Tag> tags;
        Tag newTag = new Tag("acessórios");
        Tag newTag2 = new Tag("roupas");
        Tag newTag3 = new Tag("móveis");
        Tag newTag4 = new Tag("smartphones");
        arrayOfTags.add(newTag2);
        arrayOfTags.add(newTag);
        arrayOfTags.add(newTag4);
        arrayOfTags.add(newTag3);
    }

    public void addPosts(){
        Post post = new Post();
        Post post2 = new Post();
        Post post3 = new Post();
        Post post4 = new Post();

        post.setTitle("Pão fresquinho");
        post.setSubtitle("Pense num pão bom, mais é bom, é bom mesmo!");
        post.setPrice(12.99);
//        post.setImgRef(R.drawable.image_test_1);


        post2.setTitle("Bicicleta Caloi 100");
        post2.setSubtitle("Bike semi nova, 3 meses de uso, perfeito estado, ótimo preço");
        post2.setPrice(469.99);
//        post2.setImgRef(R.drawable.image_test_2);


        post3.setTitle("Sapato salto Vizano");
        post3.setSubtitle("Sapato em ótimo estado, apenas uns 7 anos de uso, cor de carnaval, muito confortável, tipo uma pedra");
        post3.setPrice(59.99);
//        post3.setImgRef(R.drawable.image_test_3);

        post4.setTitle("Sapato salto Vizano");
        post4.setSubtitle("Sapato em ótimo estado, apenas uns 7 anos de uso, cor de carnaval, muito confortável, tipo uma pedra");
        post4.setPrice(59.99);
//        post4.setImgRef(R.drawable.image_test_1);

        posts.add(post);
        posts.add(post2);
        posts.add(post3);
        posts.add(post4);
    }


    public void negociar(View view){
        Toast.makeText(getBaseContext(),"Abrir tela de chat", Toast.LENGTH_SHORT).show();
    }

    public void unfavorite(View view){
//        favorite = (ImageButton) findViewById(R.id.favorite);
//        favorite.setColorFilter(Color.argb(255, 68, 68, 68));
        likeButton.setLiked(true);
//        Toast.makeText(getBaseContext(),"Abrir tela de chat", Toast.LENGTH_SHORT).show();
    }

}
