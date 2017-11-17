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

import java.util.ArrayList;
import java.util.List;

import br.ufc.quixada.up.Models.Post;
import br.ufc.quixada.up.Models.Tag;
import br.ufc.quixada.up.R;

/**
 * Created by Brendon on 23/10/2017.
 */

//public class ListaDesejosPostsAdapter extends BaseAdapter {
//
//    private Context context;
//    private List<Post> posts;
//
//    public ListaDesejosPostsAdapter(Context context, List<Post> posts) {
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
//        View layout = layoutInflater.inflate(R.layout.post_lista_desejos, null);
//
//        TextView title = (TextView) layout.findViewById(R.id.LD_textView_title);
//        title.setText(post.getTitle());
//
//        TextView subtitle = (TextView) layout.findViewById(R.id.LD_textView_describ);
//        subtitle.setText(post.getSubtitle());
//
//        TextView price = (TextView) layout.findViewById(R.id.LD_textView_price);
//        price.setText("R$ " + post.getPrice());
//
//        ImageView image = (ImageView) layout.findViewById(R.id.LD_imageView_photo);
//        image.setImageResource(post.getImage(i));
//
//        return layout;
//    }
//}

public class ListaDesejosPostsAdapter extends RecyclerView.Adapter<ListaDesejosPostsAdapter.MyViewHolder>{

    StringBuilder builder = new StringBuilder();
    private LayoutInflater inflater;
    private Context context;
    private List<Post> posts;
//    <your string array list>

    public ListaDesejosPostsAdapter(Context context, List<Post> posts) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.posts=posts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.post_lista_desejos, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        holder.serial_number.setText(<your string array[position]>);
//        holder.button.setText();
//        Log.d("Teste", "testeeee!!!!!!!!!!!!!!!!1");
        Post currentPost = posts.get(position);
        holder.title.setText(currentPost.getTitle());
        holder.subtitle.setText(currentPost.getSubtitle());
        holder.price.setText("R$ " + currentPost.getPrice().toString());
//        holder.image.setImageResource(currentPost.setImgRef(position));
//        holder.image.setImageResource(currentPost.getImage(position));

        Glide.with(context).load(currentPost.getDefaultImage()).into(holder.image);

    }

    @Override
    public int getItemCount() {
//        return <size of your string array list>;
        return posts.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
//        TextView serial_number;
//        Button button;
//        TextView tagtv;
        TextView title;
        TextView subtitle;
        TextView price;
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
//            serial_number = (TextView)itemView.findViewById(R.id.serialNo_CL);
//            tagtv = (TextView) itemView.findViewById(R.id.tagTv);
//            tagtv.setText();
                title = (TextView) itemView.findViewById(R.id.LD_textView_title);
//                title.setText(post.getTitle());

                subtitle = (TextView) itemView.findViewById(R.id.LD_textView_describ);
//                subtitle.setText(post.getSubtitle());

                price = (TextView) itemView.findViewById(R.id.LD_textView_price);
//                price.setText("R$ " + post.getPrice());

                image = (ImageView) itemView.findViewById(R.id.LD_imageView_photo);
//                image.setImageResource(post.getImage(i));

        }
    }
}
