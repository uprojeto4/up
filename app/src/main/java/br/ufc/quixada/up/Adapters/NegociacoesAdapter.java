package br.ufc.quixada.up.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.ufc.quixada.up.Activities.ChatActivity;
import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.R;

public class NegociacoesAdapter extends RecyclerView.Adapter<NegociacoesAdapter.NegociacaoViewHolder> {

    private ArrayList<Negociacao> negotiationSet;
    private String userId;
    Context context;

    public NegociacoesAdapter(Context c, ArrayList<Negociacao> negociacoes) {
        this.negotiationSet = negociacoes;
        this.context = c;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.userId = user.getUid();
        }
    }

    @Override
    public NegociacoesAdapter.NegociacaoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_negociacao, viewGroup, false);
        return new NegociacaoViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(NegociacoesAdapter.NegociacaoViewHolder negociacaoViewHolder, int position) {

//        Glide.with(NegociacoesAdapter.this).load(cat.getIcon()).into(negociacaoViewHolder.icon);

        Date date;

        negociacaoViewHolder.negociacao = negotiationSet.get(position);
        negociacaoViewHolder.textViewTituloNegociacao.setText(negociacaoViewHolder.negociacao.getTitle());
        negociacaoViewHolder.textViewNomeVendedorNegociacao.setText(negociacaoViewHolder.negociacao.getVendorName());
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            date = dateFormat.parse(negociacaoViewHolder.negociacao.getStartDate());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        negociacaoViewHolder.textViewdataInicioNegociacao.setText(negociacaoViewHolder.negociacao.getStartDate());
        negociacaoViewHolder.textViewLastMessage.setText(negociacaoViewHolder.negociacao.getLastMessage());

        if (negociacaoViewHolder.negociacao.getLastMessageSenderId().equals(userId)) {
            negociacaoViewHolder.replyIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return negotiationSet.size();
    }

    public void addNegociacao(Negociacao negociacao) {
        negotiationSet.add(negociacao);
        notifyItemInserted(this.negotiationSet.size());
    }

    public class NegociacaoViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTituloNegociacao;
        TextView textViewNomeVendedorNegociacao;
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