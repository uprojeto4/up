package br.ufc.quixada.up.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import br.ufc.quixada.up.Models.Category;
import br.ufc.quixada.up.R;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.CategoriaViewHolder> {

    private static ArrayList<Category> categorySet;

    public CategoriasAdapter(ArrayList<Category> categories) {
        categorySet = categories;
    }

    @Override
    public CategoriasAdapter.CategoriaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_categoria_icone, null);
        CategoriaViewHolder categoriaViewHolder = new CategoriaViewHolder(itemLayoutView);
        return categoriaViewHolder;
    }

    @Override
    public void onBindViewHolder(CategoriasAdapter.CategoriaViewHolder categoriaViewHolder, int i) {

        Category cat = categorySet.get(i);

        categoriaViewHolder.icon.setImageResource(cat.getIcon());
//        Glide.with(CategoriasAdapter.this).load(cat.getIcon()).into(categoriaViewHolder.icon);
        categoriaViewHolder.title.setText(cat.getTitle());
        categoriaViewHolder.category = cat;
    }

    @Override
    public int getItemCount() {
        return categorySet.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView title;

        private Category category;

        private CategoriaViewHolder(View itemLayoutView) {

            super(itemLayoutView);

            icon = itemLayoutView.findViewById(R.id.iconeCategoria);
            title = itemLayoutView.findViewById(R.id.nomeCategoria);

            itemLayoutView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

//                    Intent intent = new Intent(v.getContext(), PaginaDeResultados.class);
//                    v.getContext().startActivity(intent);

                    Toast.makeText(v.getContext(), category.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}