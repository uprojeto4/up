package br.ufc.quixada.up.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.ufc.quixada.up.Models.Negociacao;
import br.ufc.quixada.up.R;

public class NegociacoesAdapter extends RecyclerView.Adapter<NegociacoesAdapter.NegociacaoViewHolder> {

    private ArrayList<Negociacao> negotiationSet;

    public NegociacoesAdapter(ArrayList<Negociacao> negociacoes) {
        negotiationSet = negociacoes;
    }

    @Override
    public NegociacoesAdapter.NegociacaoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.negociacao, null);
        return new NegociacaoViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(NegociacoesAdapter.NegociacaoViewHolder negociacaoViewHolder, int i) {

//        Negociacao negociacao = negotiationSet.get(i);
//        negociacaoViewHolder.icon.setImageResource(cat.getIcon());
//        Glide.with(NegociacoesAdapter.this).load(cat.getIcon()).into(negociacaoViewHolder.icon);
//        negociacaoViewHolder.title.setText(cat.getTitle());

        negociacaoViewHolder.negociacao = negotiationSet.get(i);
    }

    @Override
    public int getItemCount() {
        return negotiationSet.size();
    }

    public void addNegociacao(Negociacao negociacao) {
        negotiationSet.add(negociacao);
    }

    public static class NegociacaoViewHolder extends RecyclerView.ViewHolder {

//        public ImageView icon;
//        public TextView title;

        private Negociacao negociacao;

        private NegociacaoViewHolder(View itemLayoutView) {

            super(itemLayoutView);

//            icon = itemLayoutView.findViewById(R.id.iconeCategoria);
//            title = itemLayoutView.findViewById(R.id.nomeCategoria);
            final TextView textViewTituloNegociacao = itemLayoutView.findViewById(R.id.textViewTituloNegociacao);
            final TextView textViewNomeVendedorNegociacao = itemLayoutView.findViewById(R.id.textViewNomeVendedorNegociacao);
            final TextView textViewdataInicioNegociacao = itemLayoutView.findViewById(R.id.dataInicioNegociacao);
            final TextView textViewMensagensNaoLidasNegociacao = itemLayoutView.findViewById(R.id.textViewMensagensNaoLidasNegociacao);

            itemLayoutView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

//                    textViewTituloNegociacao.setText(negociacao.getTitle());
//                    textViewNomeVendedorNegociacao.setText(negociacao.getVendor());
//                    textViewdataInicioNegociacao.setText(negociacao.getStartDate());
//                    textViewMensagensNaoLidasNegociacao.setText(negociacao.getUnreadMessagesCounter());

//                    Intent intent = new Intent(v.getContext(), PaginaDeResultados.class);
//                    v.getContext().startActivity(intent);

//                    Toast.makeText(v.getContext(), negociacao.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}