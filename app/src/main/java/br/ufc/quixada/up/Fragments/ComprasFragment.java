package br.ufc.quixada.up.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.ufc.quixada.up.Adapters.NegociacoesAdapter;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;

public class ComprasFragment extends Fragment {

    RecyclerView recyclerViewBuyChatList;
    LinearLayoutManager linearLayoutManager;
    private DatabaseReference dbReference;
    private String userId;
    private Negociacao negociacao;
    private NegociacoesAdapter negociacoesAdapter;
    LinearLayout noBuy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_compras, container, false);

        dbReference = FirebaseConfig.getDatabase();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        recyclerViewBuyChatList = (RecyclerView) rootView.findViewById(R.id.recyclerViewBuyChatList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewBuyChatList.setLayoutManager(linearLayoutManager);

        negociacoesAdapter = new NegociacoesAdapter(this.getContext(), new ArrayList<Negociacao>(), userId);
        recyclerViewBuyChatList.setAdapter(negociacoesAdapter);

        noBuy = rootView.findViewById(R.id.noBuy);

        getNegotiations();

        return rootView;

//        recyclerViewChatList = getView().findViewById(R.id.recyclerViewChatList);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerViewChatList.setLayoutManager(linearLayoutManager);
//
//        recyclerViewChatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

    }

    public void getNegotiations() {

        dbReference.child("negotiations").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
                    final Negociacao negociacao = messageDataSnapshot.getValue(Negociacao.class);
                    final String adId = negociacao.getAdId();

                    dbReference.child("posts").child(adId).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Post post = dataSnapshot.getValue(Post.class);
                            negociacao.setTitle(post.getTitle());
                            System.out.println("post: " + post);

                            dbReference.child("users").child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    negociacao.setVendorName(user.getNome());
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (negociacoesAdapter.getItemCount() == 0) {
            noBuy.setVisibility(View.VISIBLE);
        } else {
            noBuy.setVisibility(View.GONE);
        }

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

}
