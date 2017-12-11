package br.ufc.quixada.up.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import br.ufc.quixada.up.Interfaces.RecyclerViewOnClickListener;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.R;

import static br.ufc.quixada.up.R.layout.post_search_result;

/**
 * Created by Brendon on 08/12/2017.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<Post> posts;
    private RecyclerViewOnClickListener recyclerViewOnClickListener;
    Context context;

    public SearchResultsAdapter (Context c, ArrayList<Post> p){
        context = c;
        posts = p;
        layoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public SearchResultsAdapter.SearchResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(post_search_result, parent, false);
        SearchResultsViewHolder searchResultsViewHolder = new SearchResultsViewHolder(view);
        return searchResultsViewHolder;
    }

    @Override
    public void onBindViewHolder(SearchResultsAdapter.SearchResultsViewHolder holder, int position) {

        Post post = posts.get(position);

        DecimalFormat formatoMoeda = new DecimalFormat("##,###,###,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        formatoMoeda.setMinimumFractionDigits(2);
        formatoMoeda.setParseBigDecimal (true);
        String price = formatoMoeda.format(post.getPrice());


        Glide.with(context)
                .load(R.drawable.image_loading_square)
                .into(holder.cover);

        Log.d("imageCOver", post.getImageCover()+"");
        if(post.getImageCover()!= null){
            applyImage(post.getImageCover(), holder.cover);
            Log.d("chamou","applyImage");
        }

        holder.title.setText(post.getTitle());
        holder.desc.setText(post.getSubtitle());
        holder.price.setText("R$ " + price);
        holder.post = post;

//        ImageView img = (ImageView)findViewById(R.id.spinning_wheel_image);
//        holder.cover.setImageDrawable(R.drawable.image_loading_square);
//
//// Get the background, which has been compiled to an AnimationDrawable object.
//        AnimationDrawable frameAnimation = (AnimationDrawable) holder.cover.getDrawable();
//
//// Start the animation (looped playback by default).
//        frameAnimation.start();


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class SearchResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView desc;
        TextView price;
        ImageView cover;

        private Post post;

        public SearchResultsViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.LD_textView_title);
            desc = (TextView) itemView.findViewById(R.id.LD_textView_describ);
            price = (TextView) itemView.findViewById(R.id.LD_textView_price);
            cover = (ImageView) itemView.findViewById(R.id.LD_imageView_photo);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(recyclerViewOnClickListener != null){
                recyclerViewOnClickListener.onClickListener(view, getLayoutPosition());
            }
        }
    }

    public void setRecyclerViewOnClickListener(RecyclerViewOnClickListener recyclerViewOnClickListener) {
        this.recyclerViewOnClickListener = recyclerViewOnClickListener;
    }

    public void applyImage(byte[] bytes, ImageView imageView){
        //options para o glide
        RequestOptions requestOptions = new RequestOptions();
        //não salava a imagem em cache, para que ela possa ser alterada caso outra pessoa se logue
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);

        //carrega a imagem
        Glide.with(context).load(bytes)
                //aplica as options de cache
                .apply(requestOptions)
                //insere a imagem no imageView
                .into(imageView);
    }

}
