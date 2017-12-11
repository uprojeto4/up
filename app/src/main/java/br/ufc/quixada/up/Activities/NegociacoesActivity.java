package br.ufc.quixada.up.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import br.ufc.quixada.up.Interfaces.NegotiationFragmentDisplay;
import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.R;

public class NegociacoesActivity extends BaseActivity implements NegotiationFragmentDisplay {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public String userId;
    public NegociacoesAdapter buyAdapter;
    public NegociacoesAdapter sellAdapter;
    public static boolean isChatActivityOpened = false;
    public static String currentOpenedChatNegotiationKey;
    private Spinner spinner;
    private DatabaseReference dbReference;
    String spinnerItem;
    ArrayList<String> filters = new ArrayList<>();
    private NegociacoesFragmentPagerAdapter negociacoesFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negociacoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        negociacoesFragmentPagerAdapter = new NegociacoesFragmentPagerAdapter(getSupportFragmentManager(),
                getResources().getStringArray(R.array.tabs_negociacoes));

        viewPager.setAdapter(negociacoesFragmentPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
//                        System.out.println("Abertas");
                    } else if (position == 1) {
//                        System.out.println("Fechadas");
                    } else if (position == 2) {
//                        System.out.println("Canceladas");
                    }
//                    Toast.makeText(getApplicationContext(), "Selecionado: " + spinnerItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(getApplicationContext(), "Opçao Default ", Toast.LENGTH_SHORT).show();
            }
        });

        if(user != null){
            updateUserInfo();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_perfil, menu);
        return true;
    }

    public void manageNegotiations() {
//<<<<<<< qualificacoes-Brendon
//        if (userId != null){

            dbReference.child("negotiations").child(userId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        final Negociacao negociacao = dataSnapshot.getValue(Negociacao.class);
                        final String negotiationKey = dataSnapshot.getKey();

    //                    manageNegotiationsVisibility(Constant.SHOW_BUY_NEGOTIATIONS);

                        dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                negociacao.setTitle(dataSnapshot.child("title").getValue(String.class));
                                String remoteUserId = dataSnapshot.child("userId").getValue(String.class);

                                dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        negociacao.setVendorName(dataSnapshot.child("nome").getValue(String.class));
                                        if (negociacao.getVendorId().equals(userId)) {
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
    //
                @Override
                public void onChildChanged(final DataSnapshot dataSnapshot, String s) {

                    final Negociacao negociacao = dataSnapshot.getValue(Negociacao.class);
                    dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot postDataSnapshot) {
                            negociacao.setTitle(postDataSnapshot.child("title").getValue(String.class));
                            String remoteUserId = postDataSnapshot.child("userId").getValue(String.class);
//=======

        dbReference.child("negotiations").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Negociacao negociacao = dataSnapshot.getValue(Negociacao.class);
                final String negotiationKey = dataSnapshot.getKey();
                final String remoteUserId = negociacao.getRemoteUserId();

                dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        negociacao.setTitle(dataSnapshot.child("title").getValue(String.class));

                        dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//>>>>>>> sprint-final

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

//<<<<<<< qualificacoes-Brendon
                                @Override
                                public void onDataChange(DataSnapshot userDataSnapshot) {
                                    negociacao.setVendorName(userDataSnapshot.child("nome").getValue(String.class));
                                    final int index;
                                    System.out.println(negociacao.getUnreadMessagesCounter());
                                    if (negociacao.getVendorId().equals(userId)) {
                                        index = sellAdapter.getIndexOfKey(dataSnapshot.getKey());
                                        sellAdapter.updateNegociacao(index, negociacao);
                                    } else {
                                        index = buyAdapter.getIndexOfKey(dataSnapshot.getKey());
                                        buyAdapter.updateNegociacao(index, negociacao);
                                    }
                                    if ((!isChatActivityOpened && !negociacao.getLastMessageSenderId().equals(userId)) || (isChatActivityOpened && !dataSnapshot.getKey().equals(currentOpenedChatNegotiationKey))) {
                                        vibrate();
                                    }
//=======
                                negociacao.setVendorName(dataSnapshot.child("nome").getValue(String.class));
                                if (negociacao.getVendorId().equals(userId)) {
                                    sellAdapter.addNegociacao(negotiationKey, negociacao);
                                } else {
                                    buyAdapter.addNegociacao(negotiationKey, negociacao);
//>>>>>>> sprint-final
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

//<<<<<<< qualificacoes-Brendon
                        }
                    });
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }
//=======
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                final Negociacao negociacao = dataSnapshot.getValue(Negociacao.class);
                final String negotiationKey = dataSnapshot.getKey();

//                    manageNegotiationsVisibility(Constant.SHOW_BUY_NEGOTIATIONS);
//
                dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {
//
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        negociacao.setTitle(dataSnapshot.child("title").getValue(String.class));

                        dbReference.child("users").child(negociacao.getRemoteUserId()).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                negociacao.setVendorName(dataSnapshot.child("nome").getValue(String.class));
                                if (negociacao.getVendorId().equals(userId)) {
                                    sellAdapter.updateNegociacao(sellAdapter.getIndexOfKey(negotiationKey), negociacao);
                                } else {
                                    buyAdapter.updateNegociacao(buyAdapter.getIndexOfKey(negotiationKey), negociacao);
                                }

                                if ((!isChatActivityOpened && !negociacao.getLastMessageSenderId().equals(userId)) || (isChatActivityOpened && !negotiationKey.equals(currentOpenedChatNegotiationKey))) {
                                    vibrate();
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
//>>>>>>> sprint-final

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

//        }else{
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.no_address_dialog_title)
//                    .setMessage(NegociacoesActivity.this.getString(R.string.faca_login))
//                    .setPositiveButton(NegociacoesActivity.this.getString(R.string.sim), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //                            finish();
//                            Intent intent = new Intent(NegociacoesActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                        }
//                    }).setNegativeButton(NegociacoesActivity.this.getString(R.string.nao), null)
//                    .show();
//        }
    }
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.getChildrenCount() != 0) {
//                    final Negociacao negociacao = dataSnapshot.getValue(Negociacao.class);
//                    final String negotiationKey = dataSnapshot.getKey();
//
////                    manageNegotiationsVisibility(Constant.SHOW_BUY_NEGOTIATIONS);
//
//                    dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            negociacao.setTitle(dataSnapshot.child("title").getValue(String.class));
//                            String remoteUserId = dataSnapshot.child("userId").getValue(String.class);
//
//                            dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    System.out.println("venda: " + dataSnapshot);
//                                    negociacao.setVendorName(dataSnapshot.child("nome").getValue(String.class));
//                                    if (negociacao.getVendorId().equals(userId)) {
//                                        sellAdapter.addNegociacao(negotiationKey, negociacao);
//                                    } else {
//                                        buyAdapter.addNegociacao(negotiationKey, negociacao);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
//
//                final Negociacao negociacao = dataSnapshot.getValue(Negociacao.class);
//                dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot postDataSnapshot) {
//                        negociacao.setTitle(postDataSnapshot.child("title").getValue(String.class));
//                        String remoteUserId = postDataSnapshot.child("userId").getValue(String.class);
//
//                        dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                            @Override
//                            public void onDataChange(DataSnapshot userDataSnapshot) {
//                                negociacao.setVendorName(userDataSnapshot.child("nome").getValue(String.class));
//                                final int index;
//                                System.out.println(negociacao.getUnreadMessagesCounter());
//                                if (negociacao.getVendorId().equals(userId)) {
//                                    index = sellAdapter.getIndexOfKey(dataSnapshot.getKey());
//                                    sellAdapter.updateNegociacao(index, negociacao);
//                                } else {
//                                    index = buyAdapter.getIndexOfKey(dataSnapshot.getKey());
//                                    buyAdapter.updateNegociacao(index, negociacao);
//                                }
//                                if ((!isChatActivityOpened && !negociacao.getLastMessageSenderId().equals(userId)) || (isChatActivityOpened && !dataSnapshot.getKey().equals(currentOpenedChatNegotiationKey))) {
//                                    vibrate();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void manageNegotiationsVisibility(int control) {
        int position = tabLayout.getSelectedTabPosition();
        Fragment fragment = negociacoesFragmentPagerAdapter.getItem(tabLayout.getSelectedTabPosition());
        if (fragment != null) {
            switch (position) {
                case 0:
                    System.out.println("compras");
                    ((ComprasFragment) fragment).manageNegotiationsVisibility(control);
//                    ((ComprasFragment) fragment).noBuy.setVisibility(View.GONE);
                    break;
                case 1:
                    System.out.println("vendas");
//                    ((VendasFragment) fragment).onRefresh();
                    break;
            }
        }
    }

    public void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }
}
