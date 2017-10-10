package br.ufc.quixada.up;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ArrayList<Post> posts = new ArrayList<Post>();

//        for (int i = 0; i<5; i++){
//            Post post = new Post();
//            post.setTitle("Meu Post de num "+(i + 1));
//            post.setSubtitle("Esse post é massa d+ "+(i + 1));
//            post.setPrice(12.00);
//
//            posts.add(post);
//        }

        Post post = new Post();
        post.setTitle("Pão fresquinho");
        post.setSubtitle("pense num pão bom, mais é bom, é bom mesmo!");
        post.setPrice(12.00);

        posts.add(post);
        posts.add(post);
        posts.add(post);
        posts.add(post);
        posts.add(post);


        ListView listView = (ListView)findViewById(R.id.lv_cards);
        listView.setAdapter(new PostAdapter(this, posts));
    }
}
