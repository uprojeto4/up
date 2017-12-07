package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import br.ufc.quixada.up.Adapters.PerfilFragmentPagerAdapter;
import br.ufc.quixada.up.R;

public class PerfilPublicoActivity extends BaseActivity {

    private TabLayout perfilTabLayout;
    public static ViewPager perfilViewPager;

    public static String anuncianteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_publico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPP);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

//        Intent intent = getIntent();
//        anuncianteId = intent.getStringExtra("idAnunciante");

        perfilTabLayout = (TabLayout) findViewById(R.id.tab_layout_pp);
        perfilViewPager = (ViewPager) findViewById(R.id.view_pager_perfil_pp);

        perfilViewPager.setAdapter(new PerfilFragmentPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.tabs_perfil)));
//        perfilViewPager.setCurrentItem(fragmentASerAberta);

        perfilTabLayout.setupWithViewPager(perfilViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        anuncianteId = intent.getStringExtra("idAnunciante");

        getSupportActionBar().setTitle(intent.getStringExtra("nomeAnunciante"));

    }

    @Override
    protected void onStop() {
        super.onStop();
        anuncianteId = null;
    }
}
