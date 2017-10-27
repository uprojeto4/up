package br.ufc.quixada.up.Adapters;

import android.content.Context;
import br.ufc.quixada.up.Models.Tag;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.ufc.quixada.up.R;

/**
 * Created by Brendon on 22/10/2017.
 */
//
//public class TagAdapter extends ArrayAdapter<Tag>{
//
//    public TagAdapter( Context context, ArrayList<Tag> tags) {
//        super(context, 0, tags);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Get the data item for this position
//        Tag tag = getItem(position);
//        // Check if an existing view is being reused, otherwise inflate the view
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tag, parent, false);
//        }
//        // Lookup view for data population
//        Button button = (Button) convertView.findViewById(R.id.tagButton);
//        // Populate the data into the template view using the data object
//        button.setText(tag.title);
//        // Return the completed view to render on screen
//        return convertView;
//    }
//
//
//}

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyViewHolder>{

    StringBuilder builder = new StringBuilder();
    private LayoutInflater inflater;
    private Context context;
    private List<Tag> tags;
//    <your string array list>

    public TagAdapter(Context context, List<Tag> tags) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.tags=tags;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tag, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        holder.serial_number.setText(<your string array[position]>);
//        holder.button.setText();
        Tag currentTag = tags.get(position);
        holder.tagtv.setText(currentTag.getTitle());
    }

    @Override
    public int getItemCount() {
//        return <size of your string array list>;
        return tags.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
//        TextView serial_number;
//        Button button;
        TextView tagtv;
        public MyViewHolder(View itemView) {
            super(itemView);
//            serial_number = (TextView)itemView.findViewById(R.id.serialNo_CL);
            tagtv = (TextView) itemView.findViewById(R.id.tagTv);
//            tagtv.setText();
        }
    }
}
