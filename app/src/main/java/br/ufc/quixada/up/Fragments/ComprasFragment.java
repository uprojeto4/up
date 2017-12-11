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
import br.ufc.quixada.up.Interfaces.NegotiationFragmentDisplay;
import br.ufc.quixada.up.R;

public class ComprasFragment extends Fragment implements NegotiationFragmentDisplay {

    private RecyclerView recyclerViewBuyChatList;
    private LinearLayoutManager linearLayoutManager;
//    public LinearLayout noBuy;
    private NegociacoesActivity negociacoesActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_compras, container, false);

        negociacoesActivity = (NegociacoesActivity) getActivity();

        recyclerViewBuyChatList = (RecyclerView) rootView.findViewById(R.id.recyclerViewBuyChatList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewBuyChatList.setLayoutManager(linearLayoutManager);
//        noBuy = rootView.findViewById(R.id.noBuyLayout);

        recyclerViewBuyChatList.setAdapter(negociacoesActivity.buyAdapter);

        return rootView;
    }

    public void manageNegotiationsVisibility(int control) {
//        if (control == Constant.HIDE_BUY_NEGOTIATIONS) {
//            noBuy.setVisibility(View.VISIBLE);
//            recyclerViewBuyChatList.setVisibility(View.GONE);
//        } else if (control == Constant.SHOW_BUY_NEGOTIATIONS) {
//            System.out.println(noBuy);
//            System.out.println(recyclerViewBuyChatList);
//            noBuy.setVisibility(View.GONE);
//            recyclerViewBuyChatList.setVisibility(View.VISIBLE);
//        }
        System.out.println("ComprasFragment manageNegotiationsVisibility " + control);
    }
}
