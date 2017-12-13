package br.ufc.quixada.up.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.like.LikeButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Interfaces.RecyclerViewOnClickListener;
import br.ufc.quixada.up.Models.Constant;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Adapters.PostAdapter;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.Network;

public class MainActivity extends BaseActivity implements RecyclerViewOnClickListener{

    DatabaseReference postsReference = FirebaseConfig.getDatabase().child("posts");

    Post post;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    PostAdapter postAdapter;
    NavigationView navigationView;

    public static Post searchPost;
    String searchTerm;
    public static ArrayList<Post> searchPosts = new ArrayList<Post>();

    private int numPostsByTime = 3;
    private String lastPositionId;
    protected boolean lastPost = false;

    LikeButton likeButton;


    public static boolean isLogged;


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
                if(isLogged){
                    Intent intent = new Intent(getApplicationContext(), NovoAnuncioActivity.class);
                    startActivity(intent);
                }else{
                    new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.no_address_dialog_title)
                        .setMessage(MainActivity.this.getString(R.string.faca_login))
                        .setPositiveButton(MainActivity.this.getString(R.string.sim), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //                            finish();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton(MainActivity.this.getString(R.string.nao), null)
                        .show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_view =  navigationView.getHeaderView(0);


        if(user != null){
            updateUserInfo();
            loadFromFirebase(numPostsByTime, recyclerView);
        }

        localUserId = localUser.getId();

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

                LinearLayoutManager llm  = (LinearLayoutManager)recyclerView.getLayoutManager();
                PostAdapter pa = (PostAdapter)recyclerView.getAdapter();
                if(BaseActivity.posts.size() == llm.findLastCompletelyVisibleItemPosition()+1 && lastPost == false){
                    Toast.makeText(MainActivity.this, "Carregando ...", Toast.LENGTH_SHORT).show();
                    loadMoreFromFirebase(numPostsByTime, lastPositionId, recyclerView);
                }

            }
        });

//        postAdapter = new PostAdapter(this, BaseActivity.posts);
//        postAdapter.setRecyclerViewOnClickListener(this);

        recyclerView.setAdapter(startAdapter());

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastPost = false;
                boolean show = false;
                final Snackbar snackbar = Snackbar.make(recyclerView, "Sinto Muito! não há conexão com a internet.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }

                }).setActionTextColor(getResources().getColor(R.color.colorPrimary));

                if(Network.hasNetwork(getBaseContext())){
                    if (snackbar.isShown()){
                        snackbar.dismiss();
                    }
                    recyclerView.setAdapter(startAdapter());
                    loadFromFirebase(numPostsByTime, recyclerView);
                }else{
                    show = true;
                }

                final boolean finalShow = show;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        if(finalShow == true){
                            snackbar.show();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });

        if (localUser.getEmail() != null){
            isLogged = true;
            if (localUser.getAddress().getLogradouro().equals("") || localUser.getAddress().getNumero().equals("") ||
                    localUser.getAddress().getBairro().equals("") || localUser.getAddress().getCidade().equals("") ||
                    localUser.getAddress().getLogradouro().equals("null") || localUser.getAddress().getNumero().equals("null") ||
                    localUser.getAddress().getBairro().equals("null") || localUser.getAddress().getCidade().equals("null")){
                new AlertDialog.Builder(this)
                        .setTitle(R.string.no_address_dialog_title)
                        .setMessage(MainActivity.this.getString(R.string.insert_address_message))
                        .setPositiveButton(MainActivity.this.getString(R.string.sim), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
    //                            finish();
                                Intent intent = new Intent(MainActivity.this, EditPerfilActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton(MainActivity.this.getString(R.string.nao), null)
                        .show();
            }
        } else{
            isLogged = false;
        }

        loadNewPosts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null){
            Log.d("currentUser if", currentUser+"");
        }else {
            Log.d("currentUser else", currentUser + "");
        }
//        updateProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {

                searchTerm = query;
                postsReference.orderByChild("title").equalTo(query).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("resultados", dataSnapshot+"");
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            searchPost = singleSnapshot.getValue(Post.class);
                            searchPosts.add(searchPost);
                        }
                        Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                        intent.putExtra("searchTerm", searchTerm);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

//                postsReference.orderByChild("title").equalTo(newText).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.d("resultados", dataSnapshot+"");
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

                return false;
            }
        });

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = (MenuItem)navigationView.getMenu().findItem(R.id.nav_inicio);
        menuItem.setChecked(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseActivity.posts = new ArrayList<Post>();
        MainActivity.searchPosts = new ArrayList<Post>();
    }


    public PostAdapter startAdapter(){
        BaseActivity.posts = new ArrayList<Post>();
        postAdapter = new PostAdapter(this, BaseActivity.posts);
        postAdapter.setRecyclerViewOnClickListener(this);

        return postAdapter;
    }

    public void share(View view){
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
    }

    public void favorite(View view) {
        likeButton.setLiked(true);
    }

    public void loadFromFirebase(int num, View view){
        if(Network.hasNetwork(getBaseContext())){
            postsReference.limitToLast(num).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int last = numPostsByTime;
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        post = singleSnapshot.getValue(Post.class);

                        Log.d("TAG", "post: "+post.getTitle()+" - "+post.getId());
                        if (last == numPostsByTime){
                            lastPositionId = post.getId();
                            Log.d("TAG", "postID: "+lastPositionId);
                            last--;
                        }else{
                            recyclerView.scrollToPosition(0);
                            if(!BaseActivity.posts.contains(post)){
                                post.downloadImageCover(post.getPictures().get(0), postAdapter, post);
                            }
                            Log.d("testando", "entrou");
                            postAdapter.addTopListItem(post);
                        }
                    }
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar3);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("oops", databaseError.getMessage());
                }
            });
        }else{
            Snackbar.make(recyclerView, "Sinto Muito! não há conexão com a internet.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Settings", new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(intent);
                        }

                    }).setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
        }
    }

    public void loadMoreFromFirebase(final int num, final String position, View view){
        if(position != null && Network.hasNetwork(getBaseContext())){
            postsReference.orderByKey().endAt(lastPositionId).limitToLast(num).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Post> listAux = new ArrayList<Post>();
//                    Log.e("TAG", "novo: "+dataSnapshot.getChildrenCount()+" - "+recyclerView.getScrollY());
                    int last = numPostsByTime;
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                            post = singleSnapshot.getValue(Post.class);
                        listAux.add(singleSnapshot.getValue(Post.class));
                    }

                    for (int i=listAux.size(); i>0; i--) {
                        Log.d("TAG", i + "");

                        post = listAux.get(i - 1);
                        Log.d("TAG", "post: " + post.getTitle() + " - " + post.getId());
                        if (dataSnapshot.getChildrenCount() == numPostsByTime) {
                            if (last > 1) {
                                if (!BaseActivity.posts.contains(post)) {
                                    post.downloadImageCover(post.getPictures().get(0), postAdapter, post);
                                }
                                Log.d("testando", "entrou");
                                postAdapter.addBottomListItem(post);
                                last--;
                            } else {
                                lastPositionId = post.getId();
                                Log.d("TAG", "postID: " + lastPositionId);
                            }
                        } else {
                            if (!BaseActivity.posts.contains(post)) {
                                post.downloadImageCover(post.getPictures().get(0), postAdapter, post);
                            }
                            postAdapter.addBottomListItem(post);
                            lastPost = true;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("oops", databaseError.getMessage());
                }
            });
        }else{
            Snackbar.make(recyclerView, "Sinto Muito! não há conexão com a internet.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Settings", new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(intent);
                        }

                    }).setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
        }
    }

    public void loadNewPosts(){
        postsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if(!BaseActivity.posts.contains(dataSnapshot.getValue(Post.class))){
//                    Log.d("lepo", "entrou");
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.child("id").getValue(String.class);
                Post p = dataSnapshot.getValue(Post.class);
                int position = postAdapter.searchListItem(id);
                p.setImageCover(BaseActivity.posts.get(position).getImageCover());


                postAdapter.setListItem(p, position);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(getBaseContext(), AnuncioActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("callerId", Constant.POST_CALLER_MAIN_ACTIVITY);
        startActivity(intent);
    }
}
