package br.ufc.quixada.up;

import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import br.ufc.quixada.up.Activities.MainActivity;
import br.ufc.quixada.up.Models.Post;

public class TesteActivity extends AppCompatActivity {


    static TesteActivity testeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);
        TesteActivity.testeActivity = this;
    }

    public static TesteActivity getInstance(){
        return TesteActivity.testeActivity;
    }

    public synchronized void mudarImage(byte[] image){
        Log.d("logs34","funcionou");
        ImageView imageView = (ImageView) findViewById(R.id.minhaImagem);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0,image.length));
    }
}
