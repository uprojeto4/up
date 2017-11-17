package br.ufc.quixada.up.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.ufc.quixada.up.Adapters.NegociacoesAdapter;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.R;

public class ComprasFragment extends Fragment {

    RecyclerView recyclerViewChatList;
    LinearLayoutManager linearLayoutManager;
    private DatabaseReference dbReference;
    private String userId;
    private NegociacoesAdapter negociacoesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_compras, container, false);

        negociacoesAdapter = new NegociacoesAdapter(new ArrayList<Negociacao>());

        recyclerViewChatList = (RecyclerView) rootView.findViewById(R.id.recyclerViewChatList);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewChatList.setLayoutManager(linearLayoutManager);
        recyclerViewChatList.setAdapter(negociacoesAdapter);

        dbReference = FirebaseConfig.getDatabase();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        dbReference.child("messages-metadata").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
                    Negociacao negociacao = messageDataSnapshot.getValue(Negociacao.class);
                    Log.d("maxxx ", "" + messageDataSnapshot.getValue(Negociacao.class));
                    negociacoesAdapter.addNegociacao(negociacao);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

}
