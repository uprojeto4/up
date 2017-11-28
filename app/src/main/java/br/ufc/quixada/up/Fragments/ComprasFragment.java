package br.ufc.quixada.up.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.ufc.quixada.up.Activities.NegociacoesActivity;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.R;

public class ComprasFragment extends Fragment {

    RecyclerView recyclerViewBuyChatList;
    LinearLayoutManager linearLayoutManager;
//    private DatabaseReference dbReference;
//    private String userId;
    private Negociacao negociacao;
//    private NegociacoesAdapter negociacoesAdapter;
    private LinearLayout noBuy;
    private NegociacoesActivity negociacoesActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_compras, container, false);

//        dbReference = FirebaseConfig.getDatabase();
        negociacoesActivity = (NegociacoesActivity) getActivity();

//        userId = negociacoesActivity.userId;

        recyclerViewBuyChatList = (RecyclerView) rootView.findViewById(R.id.recyclerViewBuyChatList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewBuyChatList.setLayoutManager(linearLayoutManager);

//        negociacoesAdapter = new NegociacoesAdapter(this.getContext(), new ArrayList<Negociacao>(), userId);
//        recyclerViewBuyChatList.setAdapter(negociacoesAdapter);

        recyclerViewBuyChatList.setAdapter(negociacoesActivity.negociacoesAdapter);

        noBuy = rootView.findViewById(R.id.noBuy);

//        getNegotiations();

        return rootView;

/*        recyclerViewChatList = getView().findViewById(R.id.recyclerViewChatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewChatList.setLayoutManager(linearLayoutManager);

        recyclerViewChatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });*/

    }

//    public void getNegotiations() {
//        System.out.println("comprasFragment userId: " + userId);
//        dbReference.child("negotiations").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                System.out.println("comprasFragment dataSnapshot: " + dataSnapshot);
//                System.out.println("comprasFragment dataSnapshotChildrenCount: " + dataSnapshot.getChildrenCount());
//                if (dataSnapshot.getChildrenCount() == 0) {
//                    noBuy.setVisibility(View.VISIBLE);
//                } else {
//                    noBuy.setVisibility(View.GONE);
//                    for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
//                        final Negociacao negociacao = messageDataSnapshot.getValue(Negociacao.class);
//
//                        if (negociacao.getType().equals("buy")) {
//
//                            final String adId = negociacao.getAdId();
//
//                            dbReference.child("posts").child(negociacao.getAdId()).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    negociacao.setTitle(dataSnapshot.child("title").getValue(String.class));
//                                    String remoteUserId = dataSnapshot.child("userId").getValue(String.class);
//
//                                    dbReference.child("users").child(remoteUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            negociacao.setVendorName(dataSnapshot.child("nome").getValue(String.class));
//                                            negociacoesAdapter.addNegociacao(negociacao);
//                                            updateNegotiationsMetadata(adId);
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

//    public void updateNegotiationsMetadata(String adId) {
//        dbReference.child("negotiations").child(userId).child(adId).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
////                System.out.println("added");
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
//                    final Negociacao negociacao = messageDataSnapshot.getValue(Negociacao.class);
//                    final String adId = negociacao.getAdId();
//                    final String lastMessage = negociacao.getLastMessage();
//                    System.out.println("maxxx " + adId);
//                    System.out.println("maxxx " + lastMessage);
////                    criar método no adapter que busca pelo id e atualiza
//                }
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
}
