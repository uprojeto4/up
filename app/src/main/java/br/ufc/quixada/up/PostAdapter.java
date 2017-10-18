package br.ufc.quixada.up;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Isaac Bruno on 09/10/2017.
 */

public class PostAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Post> posts;

    public PostAdapter(Context context, ArrayList<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return posts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Post post = posts.get(i);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.post, null);

        TextView title = (TextView) layout.findViewById(R.id.textView_title);
        title.setText(post.getTitle());

        TextView subtitle = (TextView) layout.findViewById(R.id.textView_describ);
        subtitle.setText(post.getSubtitle());

        TextView price = (TextView) layout.findViewById(R.id.textView_price);
        price.setText("R$ " + post.getPrice());

        ImageView image = (ImageView) layout.findViewById(R.id.imageView3);
        image.setImageResource(post.getImage(i));

        return layout;
    }
}
