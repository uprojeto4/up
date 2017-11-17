package br.ufc.quixada.up.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.R;

import static br.ufc.quixada.up.R.layout.post;

/**
 * Created by Isaac Bruno on 09/10/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<Post> posts;
    Context context;
    StorageReference storageReference;
    StorageReference imageRef;
    Bitmap bitmap;
    byte[] pictureCover;
    ImageView imageView;
    FirebaseStorage firebaseStorage;


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

        holder.image.setImageResource(post.getDefaultImage());
        imageView = holder.image;
        holder.title.setText(post.getTitle());
        holder.subtitle.setText(post.getSubtitle());
        holder.price.setText("R$ " + post.getPrice());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView subtitle;
        TextView price;
        ImageView image;

        public PostViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textView_title);
            subtitle = (TextView) itemView.findViewById(R.id.textView_describ);
            price = (TextView) itemView.findViewById(R.id.textView_price);
            image = (ImageView) itemView.findViewById(R.id.imageView3);
        }
    }

    public void downloadImageCover(Post post){
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        Toast.makeText(context,"entrei: "+post.getPictures(), Toast.LENGTH_LONG).show();
        imageRef = storageReference.child("PostsPictures/" + post.getId() + "/" + post.getPictures().get(0));

        try{
            //cria o arquivo temporário local onde a imagem será armazenada
            final File localFile = File.createTempFile("jpg", "image");
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                //monitora o sucesso do download
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //transforma a imagem baixada em um bitmap
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    //transforma o bitmap em stream
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    //transforma o stream em um array de bytes
                    pictureCover = stream.toByteArray();
                    //método que aplica a imagem nos lugares desejsdos
//                            post.setImage(pictureCover);

                    applyImage(pictureCover);
                    Toast.makeText(context,imageRef.getName(), Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                //monitora a falha do downlaod
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,"Imagem não foi baixada", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e){
            e.printStackTrace();
            //manipular exceções
            Log.e("Main", "IOE exception");
        }
    }

    public void applyImage(byte[] bytes){

        //options para o glide
        RequestOptions requestOptions = new RequestOptions();
        //não salava a imagem em cache, para que ela possa ser alterada caso outra pessoa se logue
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);

        //carrega a imagem
        Glide.with(context).load(bytes)
                //aplica as options de cache
                .apply(requestOptions)
                //aplica as options de transformação
//                .apply(RequestOptions.bitmapTransform(multi))
                //insere a imagem no imageView
                .into(imageView);
    }

//
//    public PostAdapter(Context context, ArrayList<Post> posts){
//        this.context = context;
//        this.posts = posts;
//    }
//
//    @Override
//    public int getCount() {
//        return posts.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return posts.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//
//        Post post = posts.get(i);
//
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = layoutInflater.inflate(R.layout.post, null);
//
//        TextView title = (TextView) layout.findViewById(R.id.textView_title);
//        title.setText(post.getTitle());
//
//        TextView subtitle = (TextView) layout.findViewById(R.id.textView_describ);
//        subtitle.setText(post.getSubtitle());
//
//        TextView price = (TextView) layout.findViewById(R.id.textView_price);
//        price.setText("R$ " + post.getPrice());
//
//        ImageView image = (ImageView) layout.findViewById(R.id.imageView3);
//        image.setImageResource(post.getImage(i));
//
//        return layout;
//    }

}
