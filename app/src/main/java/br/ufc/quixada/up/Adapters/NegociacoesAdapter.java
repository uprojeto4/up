package br.ufc.quixada.up.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufc.quixada.up.Activities.ChatActivity;
import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.R;

public class NegociacoesAdapter extends RecyclerView.Adapter<NegociacoesAdapter.NegociacaoViewHolder> {

    private ArrayList<Negociacao> negotiationSet;
    private ArrayList<String> negotiationKeys;
    private String userId;
    Context context;

    public NegociacoesAdapter(Context c, String userId) {
        this.negotiationSet = new ArrayList<>();
        this.negotiationKeys = new ArrayList<>();
        this.context = c;
        this.userId = userId;
    }

    @Override
    public NegociacoesAdapter.NegociacaoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_negociacao, viewGroup, false);
        return new NegociacaoViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(NegociacoesAdapter.NegociacaoViewHolder negociacaoViewHolder, int position) {

        negociacaoViewHolder.negociacao = negotiationSet.get(position);
        negociacaoViewHolder.textViewTituloNegociacao.setText(negociacaoViewHolder.negociacao.getTitle());
        negociacaoViewHolder.textViewNomeVendedorNegociacao.setText(negociacaoViewHolder.negociacao.getVendorName());
        negociacaoViewHolder.textViewdataInicioNegociacao.setText(negociacaoViewHolder.negociacao.getStartDate());
        negociacaoViewHolder.textViewLastMessage.setText(negociacaoViewHolder.negociacao.getLastMessage());
        negociacaoViewHolder.textViewMensagensNaoLidasNegociacao.setText(String.valueOf(negociacaoViewHolder.negociacao.getUnreadMessagesCounter()));

        if (negociacaoViewHolder.negociacao.getUnreadMessagesCounter() == 0) {
            negociacaoViewHolder.linearLayoutMensagensNaoLidasNegociacao.setVisibility(View.INVISIBLE);
        }

        if (negociacaoViewHolder.negociacao.getLastMessageSenderId().equals(userId)) {
            negociacaoViewHolder.replyIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return negotiationSet.size();
    }

    public void addNegociacao(String key, Negociacao negociacao) {
        negotiationSet.add(negociacao);
        negotiationKeys.add(key);
        notifyItemInserted(this.negotiationSet.size());
    }

    public void updateNegociacao(int pos, Negociacao negociacao) {
        negotiationSet.set(pos, negociacao);
        notifyDataSetChanged();
    }

    public int getIndexOfKey(String key){
        System.out.println("negotiation keys list: " + negotiationKeys);
        return negotiationKeys.indexOf(key);
    }

    public class NegociacaoViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTituloNegociacao;
        TextView textViewNomeVendedorNegociacao;
        LinearLayout linearLayoutMensagensNaoLidasNegociacao;
        TextView textViewdataInicioNegociacao;
        TextView textViewMensagensNaoLidasNegociacao;
        TextView textViewLastMessage;
        ImageView replyIcon;

        private Negociacao negociacao;

        private NegociacaoViewHolder(View itemLayoutView) {

            super(itemLayoutView);

            textViewTituloNegociacao = itemLayoutView.findViewById(R.id.textViewTituloNegociacao);
            textViewNomeVendedorNegociacao = itemLayoutView.findViewById(R.id.textViewNomeVendedorNegociacao);
            textViewdataInicioNegociacao = itemLayoutView.findViewById(R.id.dataInicioNegociacao);
            linearLayoutMensagensNaoLidasNegociacao = itemLayoutView.findViewById(R.id.linearLayoutMensagensNaoLidasNegociacao);
            textViewMensagensNaoLidasNegociacao = itemLayoutView.findViewById(R.id.textViewMensagensNaoLidasNegociacao);
            textViewLastMessage = itemLayoutView.findViewById(R.id.lastMessage);
            replyIcon = itemLayoutView.findViewById(R.id.replyIcon);

            itemLayoutView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
                    intent.putExtra("remoteUserId", negociacao.getVendorId());
                    intent.putExtra("adId", negociacao.getAdId());
                    intent.putExtra("adTitle", negociacao.getTitle());
                    context.startActivity(intent);
                }
            });
        }
    }

}