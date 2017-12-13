package br.ufc.quixada.up.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Interfaces.RecyclerViewOnClickListener;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.Tag;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;

/**
 * Created by Brendon on 23/10/2017.
 */


public class ListaDesejosPostsAdapter extends RecyclerView.Adapter<ListaDesejosPostsAdapter.MyViewHolder>{

    StringBuilder builder = new StringBuilder();
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Post> posts = new ArrayList<Post>();
    ArrayList<String> postsIds = new ArrayList<String>();
    int posicao;
    Post currentPost;
    Post post;
    int i = 0;
    private LayoutInflater layoutInflater;
    private RecyclerViewOnClickListener recyclerViewOnClickListener;

//    <your string array list>

    public ListaDesejosPostsAdapter(Context context, ArrayList<Post> posts) {
//        inflater = LayoutInflater.from(context);
        this.context = context;
        this.posts = posts;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("tamanho", posts+"");
    }

    @Override
    public ListaDesejosPostsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.post_lista_desejos, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        Log.d("tamanhoq", posts.size()+"");
        Log.d("tamanhoqi", i+"");
        i++;
        return holder;
    }

    @Override
    public void onBindViewHolder(ListaDesejosPostsAdapter.MyViewHolder holder, int position) {
//        posicao = position;
//        Post currentPost = dataSnapshot.getValue(Post.class);
        Log.d("tamanho",  posts.size()+"");
        posicao = position;
        post = posts.get(position);

        DecimalFormat formatoMoeda = new DecimalFormat("##,###,###,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        formatoMoeda.setMinimumFractionDigits(2);
        formatoMoeda.setParseBigDecimal (true);
        String price = formatoMoeda.format(post.getPrice());

        holder.title.setText(post.getTitle());
        holder.subtitle.setText(post.getSubtitle());
        holder.price.setText("R$ " + price);
//        Glide.with(context).load(post.getDefaultImage()).into(holder.image);

        Log.d("postvido", post.getImageCover()+"");
        Glide.with(context)
                .load(R.drawable.image_loading_square)
                .into(holder.image);

        Log.d("imageCOver", post.getImageCover()+"");
        if(post.getImageCover()!= null){
            applyImage(post.getImageCover(), holder.image);
            Log.d("chamou","applyImage");
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        TextView serial_number;
//        Button button;
//        TextView tagtv;
        TextView title;
        TextView subtitle;
        TextView price;
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.lista_textView_title);
            subtitle = (TextView) itemView.findViewById(R.id.lista_textView_describ);
            price = (TextView) itemView.findViewById(R.id.lista_textView_price);
            image = (ImageView) itemView.findViewById(R.id.lista_imageView_photo);
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
