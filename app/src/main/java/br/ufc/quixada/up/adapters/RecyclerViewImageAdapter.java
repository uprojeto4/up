package br.ufc.quixada.up.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.ArrayList;
import java.util.List;

import br.ufc.quixada.up.R;

public class RecyclerViewImageAdapter extends RecyclerView.Adapter<RecyclerViewImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Image> images;
    private LayoutInflater inflater;
    private RequestOptions options;

    public RecyclerViewImageAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        images = new ArrayList<>();
        options = new RequestOptions().placeholder(R.drawable.imagepicker_image_placeholder).error(R.drawable.imagepicker_image_placeholder);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(inflater.inflate(R.layout.item_imagem_novo_anuncio, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        final Image image = images.get(position);

        Glide.with(context)
                .load(image.getPath())
                .apply(options)
                .into(holder.imageView);

//        if (position == getItemCount() - 1){
//            images.get(position - 1).
//        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setData(List<Image> images) {
        this.images.clear();
        if (images != null) {
            this.images.addAll(images);
        }
        notifyDataSetChanged();
    }

    public void addData(List<Image> imageList) {
        images.addAll(imageList);
        notifyItemInserted(this.images.size());
    }

    public void removeImage(int position) {
        images.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, images.size());
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_thumbnail);
        }
    }
}