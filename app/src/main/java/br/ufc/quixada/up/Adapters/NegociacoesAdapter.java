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

    private static ArrayList<Negociacao> negotiationSet;

    public NegociacoesAdapter(ArrayList<Negociacao> negociacoes) {
        negotiationSet = negociacoes;
    }

    @Override
    public NegociacoesAdapter.NegociacaoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_categoria_icone, null);
        NegociacaoViewHolder negociacaoViewHolder = new NegociacaoViewHolder(itemLayoutView);
        return negociacaoViewHolder;
    }

    @Override
    public void onBindViewHolder(NegociacoesAdapter.NegociacaoViewHolder negociacaoViewHolder, int i) {

        Negociacao negociacao = negotiationSet.get(i);

//        negociacaoViewHolder.icon.setImageResource(cat.getIcon());
//        Glide.with(NegociacoesAdapter.this).load(cat.getIcon()).into(negociacaoViewHolder.icon);
//        negociacaoViewHolder.title.setText(cat.getTitle());
        negociacaoViewHolder.negociacao = negociacao;
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

            itemLayoutView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

//                    Intent intent = new Intent(v.getContext(), PaginaDeResultados.class);
//                    v.getContext().startActivity(intent);

                    Toast.makeText(v.getContext(), negociacao.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}