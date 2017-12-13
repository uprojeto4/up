package br.ufc.quixada.up.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.nguyenhoanglam.imagepicker.model.Image;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import br.ufc.quixada.up.Activities.BaseActivity;
import br.ufc.quixada.up.Activities.ChatActivity;
import br.ufc.quixada.up.Activities.LoginActivity;
import br.ufc.quixada.up.Activities.MainActivity;
import br.ufc.quixada.up.Models.Constant;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.Interfaces.RecyclerViewOnClickListener;
import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.User;
import br.ufc.quixada.up.R;

import static br.ufc.quixada.up.R.layout.post;

/**
 * Created by Isaac Bruno on 09/10/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private LayoutInflater layoutInflater;
    protected ArrayList<Post> posts;
    private RecyclerViewOnClickListener recyclerViewOnClickListener;
    Context context;
    FirebaseAuth user = FirebaseAuth.getInstance();

    PostViewHolder vh1;
    View v1;


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
    public void onBindViewHolder(final PostViewHolder holder, int position) {

        vh1 = holder;

        Post post = posts.get(position);

        final int pos = position;

//        Log.d("entrou", "netrou");

        DecimalFormat formatoMoeda = new DecimalFormat("##,###,###,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        formatoMoeda.setMinimumFractionDigits(2);
        formatoMoeda.setParseBigDecimal (true);
        String price = formatoMoeda.format(post.getPrice());

        Log.d("imageCOver", post.getImageCover()+"");
        if(post.getImageCover()!= null){
            applyImage(post.getImageCover(), holder.image);
            Log.d("chamou","applyImage");
        }

        if (post.getUpsList()!= null){
            if (post.getUpsList().contains(user.getCurrentUser().getUid())){
                holder.upButton.setColorFilter(Color.argb(255, 255, 171, 0));
            } else{
                holder.upButton.setColorFilter(Color.argb(255, 102, 102, 102));
            }
        }
        holder.title.setText(post.getTitle());
        holder.subtitle.setText(post.getSubtitle());
        holder.price.setText("R$ " + price);
        holder.post = post;
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    likeButton.onClick(view);
                if (MainActivity.isLogged){
//                        post.addOnWishList(user.getCurrentUser().getUid(), post.getId());
                    DatabaseReference postRef = FirebaseConfig.getDatabase().child("users").child(user.getCurrentUser().getUid());
                            Log.d("TAG3", ""+user.getCurrentUser().getUid());
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            Log.d("single", dataSnapshot+"");
                            User u = dataSnapshot.getValue(User.class);
                            ArrayList<String> aux = u.getListaDesejos();
                            if (u.getListaDesejos().contains(holder.post.getId())){
//                                    likeButton.setLiked(false);
                                holder.likeButton.setLiked(false);
                                u.getListaDesejos().remove(u.getListaDesejos().indexOf(holder.post.getId()));
                                u.save();
                            } else{
                                holder.likeButton.setLiked(true);
                                aux.add(holder.post.getId());
//                                    likeButton.setLiked(true);
                                u.setListaDesejos(aux);
                                u.save();
                            }
//                }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else{
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(R.string.no_address_dialog_title)
                            .setMessage(view.getContext().getString(R.string.faca_login))
                            .setPositiveButton(view.getContext().getString(R.string.sim), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //                            finish();
                                    Intent intent = new Intent(v1.getContext(), LoginActivity.class);
                                    context.startActivity(intent);
                                }
                            }).setNegativeButton(view.getContext().getString(R.string.nao), null)
                            .show();
                }
            }
        });

        if (holder.post.getUserId().equals(BaseActivity.localUserId)) {
            holder.openChatButton.setVisibility(View.GONE);
            holder.markAsSelled.setVisibility(View.VISIBLE);
            holder.markAsSelled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(R.string.action_marcar_anuncio_vendido)
                            .setMessage(R.string.texto_marcar_vendido_anuncio)
                            .setPositiveButton(R.string.action_marcar_anuncio_vendido, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference post_node = FirebaseDatabase.getInstance().getReference().getRoot().child("posts").child(holder.post.getId());
                                    post_node.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(view.getContext(), "Anúncio finalizado", Toast.LENGTH_SHORT).show();
                                            removeListItem(pos);
                                        }
                                    });
                                }
                            }).setNegativeButton(R.string.cancelar, null)
                            .show();
                }
            });
            holder.buttonDelete.setVisibility(View.VISIBLE);
            holder.buttonDelete.setClickable(true);
            holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(R.string.excluir_anuncio_dialog_title)
                            .setMessage(R.string.excluir_anuncio_dialog_text)
                            .setPositiveButton(R.string.excluir_anuncio_dialog_title, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference post_node = FirebaseDatabase.getInstance().getReference().getRoot().child("posts").child(holder.post.getId());
                                    post_node.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(view.getContext(), "Anúncio removido", Toast.LENGTH_SHORT).show();
                                            removeListItem(pos);
                                        }
                                    });
                                }
                            }).setNegativeButton(R.string.cancelar, null)
                            .show();
                }
            });
        } else {
            holder.openChatButton.setVisibility(View.VISIBLE);
            holder.markAsSelled.setVisibility(View.GONE);
        }
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
        LikeButton likeButton;
        ImageView buttonDelete;

        private Button openChatButton;
        private Button markAsSelled;
        private ImageButton upButton;
        private Post post;

        public PostViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textView_title);
            subtitle = (TextView) itemView.findViewById(R.id.textView_describ);
            price = (TextView) itemView.findViewById(R.id.textView_price);
            image = (ImageView) itemView.findViewById(R.id.imageView3);
            likeButton = (LikeButton) itemView.findViewById(R.id.heart_button);
            buttonDelete = (ImageView) itemView.findViewById(R.id.buttonDelete);

            itemView.setOnClickListener(this);

            openChatButton = (Button) itemView.findViewById(R.id.buttonAnuncioNegociar);

            openChatButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v1 = v;
                    if (MainActivity.isLogged){
                        Intent intent = new Intent(v.getContext(), ChatActivity.class);
                        intent.putExtra("remoteUserId", post.getUserId());
                        intent.putExtra("adId", post.getId());
                        intent.putExtra("adTitle", post.getTitle());
                        intent.putExtra("submitDate", post.getDataCadastro());
                        intent.putExtra("negotiationType", Constant.NEGOTIATION_TYPE_BUY);
                        intent.putExtra("callerId", Constant.CHAT_CALLER_POST_ADAPTER);
                        context.startActivity(intent);
                    } else{
                        new AlertDialog.Builder(v.getContext())
                            .setTitle(R.string.no_address_dialog_title)
                            .setMessage(v.getContext().getString(R.string.faca_login))
                            .setPositiveButton(v.getContext().getString(R.string.sim), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //                            finish();
                                    Intent intent = new Intent(v1.getContext(), LoginActivity.class);
                                    context.startActivity(intent);
                                }
                            }).setNegativeButton(v.getContext().getString(R.string.nao), null)
                            .show();
                    }
                }
            });

            markAsSelled = (Button) itemView.findViewById(R.id.buttonMarcarComoVendidoPost);

            upButton = (ImageButton) itemView.findViewById(R.id.buttonUpCard);

//
//            Log.d("post", ""+post);

            upButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    post.up(user.getCurrentUser().getUid(), post.getId());
                }
            });

//            likeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    likeButton.onClick(view);
//                    likeButton.setLiked(true);
//                    if (MainActivity.isLogged){
////                        post.addOnWishList(user.getCurrentUser().getUid(), post.getId());
//                        DatabaseReference postRef = FirebaseConfig.getDatabase().child("users").child(user.getCurrentUser().getUid());
//                        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
////                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                                Log.d("single", dataSnapshot+"");
//                                User u = dataSnapshot.getValue(User.class);
//                                ArrayList<String> aux = u.getListaDesejos();
//                                if (u.getListaDesejos().contains(post.getId())){
////                                    likeButton.setLiked(false);
//                                    u.getListaDesejos().remove(u.getListaDesejos().indexOf(post.getId()));
//                                    u.save();
//                                } else{
//                                    aux.add(post.getId());
////                                    likeButton.setLiked(true);
//                                    u.setListaDesejos(aux);
//                                    u.save();
//                                }
////                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    } else{
//                        new AlertDialog.Builder(view.getContext())
//                                .setTitle(R.string.no_address_dialog_title)
//                                .setMessage(view.getContext().getString(R.string.faca_login))
//                                .setPositiveButton(view.getContext().getString(R.string.sim), new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        //                            finish();
//                                        Intent intent = new Intent(v1.getContext(), LoginActivity.class);
//                                        context.startActivity(intent);
//                                    }
//                                }).setNegativeButton(view.getContext().getString(R.string.nao), null)
//                                .show();
//                    }
//                }
//            });
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


//    Mecher com a lista de posts
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

    public void setListItem(Post post, int position){
        posts.set(position, post);
        notifyItemChanged(position);
    }

    public void removeListItem(int position) {
        posts.remove(position);
        notifyItemRemoved(position);
    }

    public int searchListItem(String id){
        for(Post i : posts) {
            if (id.equals(i.getId())) {
                return posts.indexOf(i);
            }
        }
        return -1;
    }

    public void applyImage(byte[] bytes, ImageView imageView){
        //options para o glide
        RequestOptions requestOptions = new RequestOptions();
        //não salva a imagem em cache, para que ela possa ser alterada caso outra pessoa se logue
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
