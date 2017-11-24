package br.ufc.quixada.up.Models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.google.firebase.database.DatabaseReference;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.List;

import br.ufc.quixada.up.Activities.CadastroActivity;
import br.ufc.quixada.up.DAO.FirebaseConfig;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.Base64Custom;
import br.ufc.quixada.up.Utils.FirebasePreferences;

/**
 * Created by Isaac Bruno on 09/10/2017.
            */

    public class Post {

    private String title;
    private String subtitle;
    private Double price;
    private int qtd;
    private String categoria;
    private int ups;
    private String userId;
//    private int id;

//    private int imgRef;
//    private List<Integer> imgReferences;

    public void save(Context context) {
        DatabaseReference databaseReference = FirebaseConfig.getDatabase();

        databaseReference.child("posts").push().setValue(this);
        Toast.makeText(context, "Anuncio Inserido com Sucesso!", Toast.LENGTH_SHORT).show();
    }

    public int getUps() {
        return ups;
    }

    public void setUps(int ups) {
        this.ups = ups;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    //    public int setImgRef(int img){
//        Log.d("IMAGEM!!!!!!!!!!!!!!!!!", "imagem"+img);
//        this.imgRef = img;
//        this.imgReferences.add(this.imgRef);
//        return imgRef;
//    }

    public int getImage(int i){
//        switch (i){
//            case 0:
//                return (R.drawable.image_test_1);
//
//            case 1:
//                return (R.drawable.image_test_2);
//
//            case 2:
//                return (R.drawable.image_test_3);
//
//            default:
//                return (R.drawable.default_img);
//        }
        return R.drawable.default_img;
    }

    @Override
    public String toString() {
        return "Post{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", price=" + price +
                ", qtd=" + qtd +
                ", categoria='" + categoria + '\'' +
                ", ups=" + ups +
                '}';
    }
}
