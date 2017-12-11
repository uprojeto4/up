package br.ufc.quixada.up.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.like.LikeButton;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.Utils;

import java.util.ArrayList;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Interfaces.RecyclerViewOnClickListener;
import br.ufc.quixada.up.Models.Message;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Adapters.PostAdapter;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.TesteActivity;
import br.ufc.quixada.up.Utils.ChatControl;
import br.ufc.quixada.up.Utils.FirebasePreferences;
import br.ufc.quixada.up.Utils.Network;

public class MainActivity extends BaseActivity implements RecyclerViewOnClickListener{

    DatabaseReference postsReference = FirebaseConfig.getDatabase().child("posts");

//    ArrayList<Post> posts = new ArrayList<Post>();
    Post post;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    PostAdapter postAdapter;

    private int numPostsByTime = 3;
    private String lastPositionId;
    private boolean lastPost = false;
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
            loadFromFirebase(numPostsByTime, recyclerView);
        }

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

        if (localUser.getAddress().getLogradouro().equals("") || localUser.getAddress().getNumero().equals("") ||
                localUser.getAddress().getBairro().equals("") || localUser.getAddress().getCidade().equals("")){
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

        loadNewPosts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseActivity.posts = new ArrayList<Post>();
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
                                post.downloadImages(post.getPictures().get(0), postAdapter, post);
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
                                    post.downloadImages(post.getPictures().get(0), postAdapter, post);
                                }
                                Log.d("testando", "entrou");
                                postAdapter.addBottomListItem(post);
                                last--;
                            } else {
                                lastPositionId = post.getId();
                                Log.d("TAG", "postID: " + lastPositionId);
                            }
                        } else {
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
//                if(!posts.contains(dataSnapshot)){
//                    post.downloadImages(post.getPictures().get(0), postAdapter, posts.indexOf(post));
//                }
                Log.d("lepo", "entrou");
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
        Log.d("posicao", position+"");
        startActivity(intent);
    }
}
