package br.ufc.quixada.up.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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

public class MainActivity extends BaseActivity implements RecyclerViewOnClickListener{

    DatabaseReference postsReference = FirebaseConfig.getDatabase().child("posts");

    ArrayList<Post> posts = new ArrayList<Post>();
    Post post;
    RecyclerView recyclerView;
    PostAdapter postAdapter;

    private int numPostsByTime = 3;
    private String lastPositionId;
    private boolean lastPost = false;
    LikeButton likeButton;

    static  MainActivity mainActivity;


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
            loadFromFirebase(numPostsByTime);
        }


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

                LinearLayoutManager llm  = (LinearLayoutManager)recyclerView.getLayoutManager();
                PostAdapter pa = (PostAdapter)recyclerView.getAdapter();
                if(posts.size() == llm.findLastCompletelyVisibleItemPosition()+1 && lastPost == false){
                    Toast.makeText(MainActivity.this, "Carregando ...", Toast.LENGTH_SHORT).show();
                    loadMoreFromFirebase(numPostsByTime, lastPositionId);
                }

            }
        });

        postAdapter = new PostAdapter(this, posts);
        postAdapter.setRecyclerViewOnClickListener(this);
        recyclerView.setAdapter(postAdapter);
        MainActivity.mainActivity = this;
    }

    public static MainActivity getInstance(){
        return MainActivity.mainActivity;
    }

//    public synchronized void mudarImage(String msg){
//        TextView textView = (TextView) findViewById(R.id.textView9);
//        textView.setText(msg);
//    }


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

    public void up(Boolean b){
//        Toast.makeText(getBaseContext(),"Dar um up maroto: "+post.getUps(), Toast.LENGTH_SHORT).show();
        ImageButton imageButtonUp = (ImageButton)findViewById(R.id.buttonUpCard);
        if(b == true){
            imageButtonUp.setColorFilter(Color.argb(255, 255, 171, 0));
        }else {
            imageButtonUp.setColorFilter(Color.argb(255, 102, 102, 102));
        }
    }

/*    public void negociar(View view){
//        Toast.makeText(getBaseContext(),"Abrir tela de chat", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("remoteUserId", "YnJlbmRvbkBnbWFpbC5jb20=");
        intent.putExtra("adId", "-Kz7OnP9IF00E0jPBxTh");
        startActivity(intent);
//        ChatControl.startConversation("remoteUserId", "productId");
    }*/

    public void favorite(View view) {
//        favorite = (ImageButton) findViewById(R.id.favorite);
//        favorite.setColorFilter(Color.argb(255, 68, 68, 68));
        likeButton.setLiked(true);
//        Toast.makeText(getBaseContext(),"Abrir tela de chat", Toast.LENGTH_SHORT).show();
    }

    public void loadFromFirebase(int num){
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
                        if(!posts.contains(post)){
                            post.downloadImages(post.getPictures().get(0), postAdapter, post);
                        }
                        Log.d("testando", "entrou");
                        postAdapter.addTopListItem(post);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("oops", databaseError.getMessage());
            }
        });
    }

    public void loadMoreFromFirebase(final int num, final String position){
        if(position != null){
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
                                if (!posts.contains(post)) {
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
        startActivity(intent);
    }
}
