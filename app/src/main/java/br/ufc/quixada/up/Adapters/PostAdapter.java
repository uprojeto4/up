package br.ufc.quixada.up.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import br.ufc.quixada.up.Activities.ChatActivity;
import br.ufc.quixada.up.Activities.MainActivity;
import br.ufc.quixada.up.Interfaces.RecyclerViewOnClickListener;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.R;

import static br.ufc.quixada.up.R.layout.post;

/**
 * Created by Isaac Bruno on 09/10/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<Post> posts;
    private RecyclerViewOnClickListener recyclerViewOnClickListener;
    Context context;
    StorageReference storageReference;
    StorageReference imageRef;
    Bitmap bitmap;
    byte[] pictureCover;
//    ImageView imageView;
    FirebaseStorage firebaseStorage;
    FirebaseAuth user = FirebaseAuth.getInstance();


    public PostAdapter(Context c, ArrayList<Post> p){
        context = c;
        posts = p;
        layoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(post, parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        Post post = posts.get(position);

        Log.d("entrou", "netrou");

        DecimalFormat formatoMoeda = new DecimalFormat("##,###,###,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        formatoMoeda.setMinimumFractionDigits(2);
        formatoMoeda.setParseBigDecimal (true);
        String price = formatoMoeda.format(post.getPrice());

//        holder.image.setImageResource(post.getDefaultImage());
//        imageView = holder.image;
        Log.d("imageCOver", post.getImageCover()+"");
        if(post.getImageCover()!= null){
            applyImage(post.getImageCover(), holder.image);
            Log.d("chamou","applyImage");
        }
        holder.title.setText(post.getTitle());
        holder.subtitle.setText(post.getSubtitle());
        holder.price.setText("R$ " + price);
        holder.post = post;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView subtitle;
        TextView price;
        ImageView image;
        private Button openChatButton;
        private ImageButton upButton;
        private Post post;

        public PostViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textView_title);
            subtitle = (TextView) itemView.findViewById(R.id.textView_describ);
            price = (TextView) itemView.findViewById(R.id.textView_price);
            image = (ImageView) itemView.findViewById(R.id.imageView3);

            itemView.setOnClickListener(this);

            openChatButton = (Button) itemView.findViewById(R.id.buttonAnuncioNegociar);

            openChatButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
                    intent.putExtra("remoteUserId", post.getUserId());
                    intent.putExtra("adId", post.getId());
                    intent.putExtra("adTitle", post.getTitle());
                    context.startActivity(intent);
                }
            });

            upButton = (ImageButton) itemView.findViewById(R.id.buttonUpCard);

            upButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
//                    intent.putExtra("remoteUserId", post.getUserId());
//                    intent.putExtra("adId", post.getId());
//                    intent.putExtra("adTitle", post.getTitle());
//                    context.startActivity(intent);

                    post.up(user.getCurrentUser().getUid(), post.getId());
//                    if(post.getUpsList().contains(user.getCurrentUser().getUid())){
//                        upButton.setColorFilter(Color.argb(255, 255, 171, 0));
//                    } else {
//                        upButton.setColorFilter(Color.argb(255, 136, 136, 136));
//                    }

                }
            });
        }

        @Override
        public void onClick(View v) {
            if(recyclerViewOnClickListener != null){
                recyclerViewOnClickListener.onClickListener(v, getLayoutPosition());
            }
        }
    }

    public void setRecyclerViewOnClickListener(RecyclerViewOnClickListener recyclerViewOnClickListener) {
        this.recyclerViewOnClickListener = recyclerViewOnClickListener;
    }

    public void addBottomListItem(Post post){
        posts.add(post);
        notifyItemInserted(posts.size());
    }

    public void addTopListItem(Post post){
        posts.add(0, post);
        notifyItemInserted(0);
    }

    public void addListItem(Post post, int position){
        posts.add(position, post);
        notifyItemInserted(position);
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
