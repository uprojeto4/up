package br.ufc.quixada.up.Activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

import br.ufc.quixada.up.Adapters.CategoriasAdapter;
import br.ufc.quixada.up.Models.Category;
import br.ufc.quixada.up.R;

public class CategoriasActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private String[] categorias;
    private int[] icones;
    private ArrayList<Category> arrayListCategorias;
    private CategoriasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.recyclerViewCategorias);
        generateCategories();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CategoriasAdapter(arrayListCategorias);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void generateCategories() {

        categorias = getResources().getStringArray(R.array.categorias);
        icones = new int[]{R.drawable.ic_sync_black_24dp,
                           R.drawable.ic_add_circle_black_24dp,
                           R.drawable.ic_exit_to_app_black_24dp,
                           R.drawable.ic_menu_share,
                           R.drawable.ic_favorite_black_24dp,
                           R.drawable.ic_menu_manage,
                           R.drawable.ic_menu_send,
                           R.drawable.ic_menu_slideshow,
                           R.drawable.ic_format_list_bulleted_black_24dp,
                           R.drawable.ic_photo_black_24dp,
                           R.drawable.ic_star_black_24dp};

        arrayListCategorias = new ArrayList<Category>();
        for (int i = 0; i < categorias.length; i++) {
            Log.d("cat i", i + " " + categorias[i]);
            Category category = new Category();
            category.setIcon(icones[i]);
            category.setTitle(categorias[i]);
            arrayListCategorias.add(category);
        }
    }

}
