package br.ufc.quixada.up;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import br.ufc.quixada.up.Activities.AnuncioActivity;

public class PhotoActivity extends AppCompatActivity {

    int index;
    ImageView imageView;
    ArrayList<byte[]> pictures = AnuncioActivity.pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setTitle("Photos");

        imageView = (ImageView)findViewById(R.id.imageView);

        Intent intent = getIntent();
        if(intent != null){
            index = intent.getIntExtra("index",-1);
            applyImage(pictures.get(index), imageView);
        }

    }

    public void prevImage(View view){
        if(index > 0){
            index -= 1;
        }
        applyImage(pictures.get(index), imageView);
    }

    public void nextImage(View view){
        if(index < pictures.size()-1){
            index += 1;
        }
        applyImage(pictures.get(index), imageView);
    }

    public void applyImage(byte[] imagem, ImageView  image){
        RequestOptions requestOptions = new RequestOptions();
        //não salava a imagem em cache, para que ela possa ser alterada caso outra pessoa se logue
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        Glide.with(this).load(imagem)
                .apply(requestOptions)
                .into(image);
    }
}
