package br.ufc.quixada.up.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufc.quixada.up.Adapters.NegociacoesAdapter;
import br.ufc.quixada.up.Adapters.NegociacoesFragmentPagerAdapter;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Fragments.ComprasFragment;
import br.ufc.quixada.up.Models.Message;
import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.R;

public class NegociacoesActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public String userId;
    public NegociacoesAdapter buyAdapter;
    public NegociacoesAdapter sellAdapter;
    private Spinner spinner;
    private DatabaseReference dbReference;
    String spinnerItem;
    ArrayList<String> filters = new ArrayList<>();
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negociacoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        viewPager.setAdapter(new NegociacoesFragmentPagerAdapter(getSupportFragmentManager(),
                getResources().getStringArray(R.array.tabs_negociacoes)));

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                // Here's your instance
////                ComprasFragment fragment =(ComprasFragment) negociacoesP.getRegisteredFragment(lastSelectedPosition);
////                // Here're your details. You can update.
////                YourDetails details = yourFragment.getDetils();
////                lastSelectedPosition = position;
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        tabLayout.setupWithViewPager(viewPager);

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

        dbReference = FirebaseConfig.getDatabase();
        userId = localUser.getId();
        buyAdapter = new NegociacoesAdapter(this, userId);
        sellAdapter = new NegociacoesAdapter(this, userId);
        manageNegotiations();

        filters.addAll(Arrays.asList(getResources().getStringArray(R.array.spinner_negociacoes)));
        spinner = (Spinner) findViewById(R.id.spinnerFilter);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_dropdown_item, filters);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position >= 0) {
                    spinnerItem = (String) adapterView.getItemAtPosition(position);
                    if (position == 0) {
                        System.out.println("Abertas");
                    } else if (position == 1) {
                        System.out.println("Fechadas");
                    } else if (position == 2) {
                        System.out.println("Canceladas");
                    }
//                    Toast.makeText(getApplicationContext(), "Selecionado: " + spinnerItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(getApplicationContext(), "Opçao Default ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = (MenuItem)navigationView.getMenu().findItem(R.id.nav_negociacoes);
        menuItem.setChecked(true);
    }

    public void manageNegotiations() {
        dbReference.child("negotiations").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getChildrenCount() == 0) {

                } else {
//                    noBuy.setVisibility(View.GONE);
                    final Negociacao negociacao = dataSnapshot.getValue(Negociacao.class);
                    final String negotiationKey = dataSnapshot.getKey();

                    dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            negociacao.setTitle(dataSnapshot.child("title").getValue(String.class));
                            String remoteUserId = dataSnapshot.child("userId").getValue(String.class);

                            dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    negociacao.setVendorName(dataSnapshot.child("nome").getValue(String.class));
                                    System.out.println(negociacao.getUnreadMessagesCounter());
                                    if (negociacao.getRemoteUserId().equals(userId)) {
                                        sellAdapter.addNegociacao(negotiationKey, negociacao);
                                    } else {
                                        buyAdapter.addNegociacao(negotiationKey, negociacao);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
                final Negociacao negociacao = dataSnapshot.getValue(Negociacao.class);

                dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot postDataSnapshot) {
                        negociacao.setTitle(postDataSnapshot.child("title").getValue(String.class));
                        String remoteUserId = postDataSnapshot.child("userId").getValue(String.class);

                        dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot userDataSnapshot) {
                                negociacao.setVendorName(userDataSnapshot.child("nome").getValue(String.class));
                                final int index;
                                if (negociacao.getRemoteUserId().equals(userId)) {
                                    index = sellAdapter.getIndexOfKey(dataSnapshot.getKey());
                                    sellAdapter.updateNegociacao(index, negociacao);
                                } else {
                                    index = buyAdapter.getIndexOfKey(dataSnapshot.getKey());
                                    buyAdapter.updateNegociacao(index, negociacao);
                                }
                                if (Build.VERSION.SDK_INT >= 26) {
                                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
                                } else {
                                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
}
