package br.ufc.quixada.up.Activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import br.ufc.quixada.up.Models.User;
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

    NavigationView navigationView;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(user != null) {
            recyclerView = findViewById(R.id.recyclerViewCategorias);
            generateCategories();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);

            adapter = new CategoriasAdapter(arrayListCategorias, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
        }

        if(user != null){
            updateUserInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = (MenuItem)navigationView.getMenu().findItem(R.id.nav_categorias);
        menuItem.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_perfil, menu);
        return true;
    }

    private void generateCategories() {

        categorias = getResources().getStringArray(R.array.categorias);
        icones = new int[]{R.drawable.ic_silverware,
                           R.drawable.ic_school_black_24dp,
                           R.drawable.ic_phone_android_black_24dp,
                           R.drawable.ic_work_black_24dp,
                           R.drawable.ic_directions_bike_black_24dp,
                           R.drawable.ic_fridge_filled,
                           R.drawable.ic_home_variant_black,
                           R.drawable.ic_weekend_black_24dp,
                           R.drawable.ic_guitar_acoustic,
                           R.drawable.ic_pets_black_24dp,
                           R.drawable.ic_soy_sauce,
                           R.drawable.ic_directions_car_black_24dp,
                           R.drawable.ic_t_shirt_silhouette};

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
