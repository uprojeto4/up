package br.ufc.quixada.up.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufc.quixada.up.Activities.ChatActivity;
import br.ufc.quixada.up.Models.Constant;
import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.DateTimeControl;

public class NegociacoesAdapter extends RecyclerView.Adapter<NegociacoesAdapter.NegociacaoViewHolder> {

    private ArrayList<Negociacao> negotiationSet, filteredNegotiations;
    private ArrayList<String> negotiationKeys;
    private String userId;
    Context context;

    public NegociacoesAdapter(Context c, String userId) {
        this.negotiationSet = new ArrayList<>();
        this.filteredNegotiations = new ArrayList<>();
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
        negociacaoViewHolder.textViewLastMessage.setText(negociacaoViewHolder.negociacao.getLastMessage());
        negociacaoViewHolder.textViewLastmessageTime.setText(DateTimeControl.generateChatTimestamp(negociacaoViewHolder.negociacao.getLastMessageTime()));
        negociacaoViewHolder.textViewMensagensNaoLidasNegociacao.setText(String.valueOf(negociacaoViewHolder.negociacao.getUnreadMessagesCounter()));
        negociacaoViewHolder.negotiationKey = negotiationKeys.get(position);
        if (negociacaoViewHolder.negociacao.getUnreadMessagesCounter() == 0) {
            negociacaoViewHolder.linearLayoutMensagensNaoLidasNegociacao.setVisibility(View.GONE);
        } else {
            negociacaoViewHolder.linearLayoutMensagensNaoLidasNegociacao.setVisibility(View.VISIBLE);
        }

        if (negociacaoViewHolder.negociacao.getVendorId().equals(userId)) {
            negociacaoViewHolder.textViewNomeVendedorNegociacao.setText("Comprador: " + negociacaoViewHolder.negociacao.getVendorName());
            negociacaoViewHolder.negotiationType = Constant.NEGOTIATION_TYPE_SELL;
        } else {
            negociacaoViewHolder.textViewNomeVendedorNegociacao.setText("Vendedor: " + negociacaoViewHolder.negociacao.getVendorName());
            negociacaoViewHolder.negotiationType = Constant.NEGOTIATION_TYPE_BUY;
        }

        if (negociacaoViewHolder.negociacao.getLastMessageSenderId().equals(userId)) {
            negociacaoViewHolder.responseIcon.setRotation(0);
            negociacaoViewHolder.responseIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_response_24dp));
            negociacaoViewHolder.textViewLastMessage.setTextColor(Color.parseColor("#808080"));
        } else {
            negociacaoViewHolder.responseIcon.setRotation(180);
            negociacaoViewHolder.responseIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_reply_24dp));
            negociacaoViewHolder.textViewLastMessage.setTextColor(Color.parseColor("#FF00948C"));
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
        return negotiationKeys.indexOf(key);
    }

    public void filterByStatus(final int status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                filteredNegotiations.clear();
                switch (status) {
                    case Constant.OPENED_NEGOTIATION:
                        for (Negociacao negociacao : negotiationSet) {
                            if (negociacao.getStatus() == Constant.OPENED_NEGOTIATION) {
                                filteredNegotiations.add(negociacao);
                            }
                        }
                        break;
                    case Constant.CLOSED_NEGOTIATION:
                        for (Negociacao negociacao : negotiationSet) {
                            if (negociacao.getStatus() == Constant.CLOSED_NEGOTIATION) {
                                filteredNegotiations.add(negociacao);
                            }
                        }
                        break;
                    case Constant.CANCELLED_NEGOTIATION:
                        for (Negociacao negociacao : negotiationSet) {
                            if (negociacao.getStatus() == Constant.CANCELLED_NEGOTIATION) {
                                filteredNegotiations.add(negociacao);
                            }
                        }
                        break;
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public class NegociacaoViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTituloNegociacao;
        TextView textViewNomeVendedorNegociacao;
        LinearLayout linearLayoutMensagensNaoLidasNegociacao;
        TextView textViewMensagensNaoLidasNegociacao;
        TextView textViewLastMessage;
        TextView textViewLastmessageTime;
        ImageView responseIcon;
        ImageView adPicture;
        String negotiationKey;
        int negotiationType;

        private Negociacao negociacao;

        private NegociacaoViewHolder(View itemLayoutView) {

            super(itemLayoutView);

            textViewTituloNegociacao = itemLayoutView.findViewById(R.id.textViewTituloNegociacao);
            textViewNomeVendedorNegociacao = itemLayoutView.findViewById(R.id.textViewNegocianteChat);
            linearLayoutMensagensNaoLidasNegociacao = itemLayoutView.findViewById(R.id.linearLayoutMensagensNaoLidasNegociacao);
            textViewMensagensNaoLidasNegociacao = itemLayoutView.findViewById(R.id.textViewMensagensNaoLidasNegociacao);
            textViewLastMessage = itemLayoutView.findViewById(R.id.lastMessage);
            textViewLastmessageTime = itemLayoutView.findViewById(R.id.lastMessageTime);
            responseIcon = itemLayoutView.findViewById(R.id.responseIcon);
            adPicture = itemLayoutView.findViewById(R.id.adPicturePreview);

            itemLayoutView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
                    intent.putExtra("remoteUserId", negociacao.getRemoteUserId());
                    intent.putExtra("adId", negociacao.getAdId());
                    intent.putExtra("adTitle", negociacao.getTitle());
                    intent.putExtra("negotiationKey", negotiationKey);
                    intent.putExtra("negotiationType", negotiationType);
                    intent.putExtra("submitDate", negociacao.getStartDate());
                    intent.putExtra("callerId", Constant.CHAT_CALLER_NEGOTIATION_ADAPTER);
                    context.startActivity(intent);
                }
            });
        }
    }

}