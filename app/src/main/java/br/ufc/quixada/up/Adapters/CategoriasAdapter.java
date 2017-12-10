package br.ufc.quixada.up.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.ufc.quixada.up.Activities.MainActivity;
import br.ufc.quixada.up.Activities.SearchResultsActivity;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Models.Category;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.R;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.CategoriaViewHolder> {

    private static ArrayList<Category> categorySet;
    private static Context context;
    static View v1;

    public CategoriasAdapter(ArrayList<Category> categories, Context c) {
        categorySet = categories;
        context = c;
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
                    DatabaseReference postsReference = FirebaseConfig.getDatabase().child("posts");


                    v1 = v;
                    postsReference.orderByChild("categoria").equalTo(category.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        public ArrayList<Post> searchPosts = new ArrayList<Post>();
//                        public Post searchPost;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("resultados", dataSnapshot+"");
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                MainActivity.searchPost = singleSnapshot.getValue(Post.class);
                                MainActivity.searchPosts.add(MainActivity.searchPost);
                            }
//                            Toast.makeText(v1.getContext(), category.getTitle(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(v1.getContext(), SearchResultsActivity.class);
                            intent.putExtra("searchTerm", category.getTitle());
                            context.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        }
    }

}