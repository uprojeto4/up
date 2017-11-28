package br.ufc.quixada.up.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import br.ufc.quixada.up.Models.Message;
import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.R;

public class NegociacoesActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public String userId;
    public NegociacoesAdapter negociacoesAdapter;
    private Spinner spinner;
    private DatabaseReference dbReference;
    String spinnerItem;
    ArrayList<String> filters = new ArrayList<>();

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

//        Button openNegociacao = (Button) findViewById(R.id.openNegociacaoButton);
//        openNegociacao.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), NegociacaoActivity.class);
//                startActivity(intent);
//            }
//        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbReference = FirebaseConfig.getDatabase();
        userId = localUser.getId();
        negociacoesAdapter = new NegociacoesAdapter(this, new ArrayList<Negociacao>(), userId);
        getNegotiations();

        filters.addAll(Arrays.asList(getResources().getStringArray(R.array.spinner_negociacoes)));
        spinner = (Spinner) findViewById(R.id.spinnerFilter);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_dropdown_item, filters);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position >= 0) {
                    spinnerItem = (String) adapterView.getItemAtPosition(position);
//                    Toast.makeText(getApplicationContext(), "Selecionado: " + spinnerItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(getApplicationContext(), "Opçao Default ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getNegotiations() {
        System.out.println("comprasFragment userId: " + userId);
        dbReference.child("negotiations").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("comprasFragment dataSnapshot: " + dataSnapshot);
                System.out.println("comprasFragment dataSnapshotChildrenCount: " + dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
                        final Negociacao negociacao = messageDataSnapshot.getValue(Negociacao.class);

                        if (negociacao.getType().equals("buy")) {

                            final String adId = negociacao.getAdId();

                            dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    negociacao.setTitle(dataSnapshot.child("title").getValue(String.class));
                                    String remoteUserId = dataSnapshot.child("userId").getValue(String.class);

                                    dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            negociacao.setVendorName(dataSnapshot.child("nome").getValue(String.class));
                                            negociacoesAdapter.addNegociacao(negociacao);
                                            updateNegotiationsMetadata(adId);
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateNegotiationsMetadata(String adId) {
        dbReference.child("negotiations").child(userId).child(adId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                System.out.println("added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
                    final Negociacao negociacao = messageDataSnapshot.getValue(Negociacao.class);
                    final String adId = negociacao.getAdId();
                    final String lastMessage = negociacao.getLastMessage();
                    System.out.println("maxxx " + adId);
                    System.out.println("maxxx " + lastMessage);
//                    criar método no adapter que busca pelo id e atualiza
                }
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

//    public void openChat(View view){
//        Intent intent = new Intent(this, ChatActivity.class);
//        startActivity(intent);
//    }

}
