package br.ufc.quixada.up.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.ufc.quixada.up.Activities.NegociacoesActivity;
import br.ufc.quixada.up.R;

public class VendasFragment extends Fragment {

    private NegociacoesActivity negociacoesActivity;
    private RecyclerView recyclerViewSellChatList;
    private LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vendas, container, false);

        negociacoesActivity = (NegociacoesActivity) getActivity();

        recyclerViewSellChatList = (RecyclerView) rootView.findViewById(R.id.recyclerViewSellChatList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewSellChatList.setLayoutManager(linearLayoutManager);

        recyclerViewSellChatList.setAdapter(negociacoesActivity.negociacoesAdapter);

        return rootView;
    }
}