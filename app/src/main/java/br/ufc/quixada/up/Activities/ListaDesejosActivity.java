package br.ufc.quixada.up.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ufc.quixada.up.Adapters.ListaDesejosPostsAdapter;
import br.ufc.quixada.up.Adapters.TagAdapter;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Fragments.FragmentBase;
import br.ufc.quixada.up.Interfaces.RecyclerViewOnClickListener;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.Tag;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;

import br.ufc.quixada.up.Activities.MainActivity;

public class ListaDesejosActivity extends BaseActivity implements RecyclerViewOnClickListener {


    RecyclerView myList;

    RecyclerView postsList;

    TagAdapter tagAdapter;

    ListaDesejosPostsAdapter listaDesejosPostsAdapter;

    public static ArrayList<Post> posts = new ArrayList<Post>();

    ArrayList<String> postsIds  = new ArrayList<String>();

    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

    LinearLayoutManager postsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

    ImageButton favorite;

    LikeButton likeButton;

    private Spinner spinner;

    String spinnerItem;

    ArrayList<String> filters = new ArrayList<>();

    NavigationView navigationView;

    String postsDesejadosIds;

    int i = 0;


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


        FirebaseConfig.getDatabase().child("users").child(localUser.getId()).child("listaDesejos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ajuda", dataSnapshot+"");
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    if(singleSnapshot.getValue(String.class) == null){
                        new AlertDialog.Builder(ListaDesejosActivity.this)
                                .setTitle(R.string.no_address_dialog_title)
                                .setMessage(ListaDesejosActivity.this.getString(R.string.nao_salvo))
                                .setPositiveButton(ListaDesejosActivity.this.getString(R.string.sim), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //                            finish();
                                        Intent intent = new Intent(ListaDesejosActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton(ListaDesejosActivity.this.getString(R.string.nao), null)
                                .show();

                    }else{
                        FirebaseConfig.getDatabase().child("posts").child(singleSnapshot.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Post currentPost = dataSnapshot.getValue(Post.class);
                                if (dataSnapshot.getValue() == null){
                                    FirebaseConfig.getDatabase().child("users").child(localUser.getId()).child("listaDesejos").child(i+"").setValue(null);
                                }else{
                                    posts.add(currentPost);
                                }
                                listaDesejosPostsAdapter = new ListaDesejosPostsAdapter(ListaDesejosActivity.this, posts);
                                listaDesejosPostsAdapter.setRecyclerViewOnClickListener(ListaDesejosActivity.this);
                                postsList.setAdapter(listaDesejosPostsAdapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    i++;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postsList = (RecyclerView) findViewById(R.id.posts_recycler_view);
        postsList.setLayoutManager(postsLayoutManager);
        postsList.setNestedScrollingEnabled(false);


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

        if (posts.size() == 0){
//            notFound.setVisibility(View.VISIBLE);
        } else{
            for (Post post : posts){
                post.downloadImagesForSearchResult(post.getPictures().get(0), listaDesejosPostsAdapter, post);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = (MenuItem)navigationView.getMenu().findItem(R.id.nav_lista_desejos);
        menuItem.setChecked(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        posts = new ArrayList<Post>();
    }

    @Override
    protected void onPause() {
        super.onPause();
        posts = new ArrayList<Post>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_perfil, menu);
        return true;
    }


    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(getBaseContext(), AnuncioActivity.class);
        intent.putExtra("positionSearch", position);
        Log.d("posicao", position+"");
        startActivity(intent);
    }
}
