package br.ufc.quixada.up;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;

import br.ufc.quixada.up.Adapters.perfilFragmentPagerAdapater;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static br.ufc.quixada.up.R.drawable.image_test_1;

public class PerfilActivity extends BaseActivity {

    private TabLayout perfilTabLayout;
    private ViewPager perfilViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        perfilTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        perfilViewPager = (ViewPager) findViewById(R.id.view_pager_perfil);

        perfilViewPager.setAdapter(new perfilFragmentPagerAdapater(getSupportFragmentManager(), getResources().getStringArray(R.array.tabs_perfil)));

        perfilTabLayout.setupWithViewPager(perfilViewPager);

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

//       ImageView imageview = (ImageView) findViewById(R.id.header_cover_image);
//
//        BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//        Bitmap blurred = blurRenderScript(this, image_test_1, radiusArr[position]);//second parametre is radius
//        imageview.setImageBitmap(blurred);

//        MultiTransformation multi = new MultiTransformation(
//                new BlurTransformation(25));
//
//        Glide.with(this).load(image_test_1)
//                .apply(RequestOptions.bitmapTransform(multi))
//                .into((ImageView) findViewById((R.id.header_cover_image)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.perfil, menu);
        return true;
    }

}
