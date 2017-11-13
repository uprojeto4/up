package br.ufc.quixada.up.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.R;

/**
 * Created by Isaac Bruno on 09/10/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<Post> posts;

    public PostAdapter(Context c, ArrayList<Post> p){
        posts = p;
        layoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.post, parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        Post post = posts.get(position);

        holder.image.setImageResource(post.getImage(position));
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
