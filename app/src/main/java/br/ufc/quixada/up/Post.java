package br.ufc.quixada.up;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.engine.Resource;

/**
 * Created by Isaac Bruno on 09/10/2017.
 */

public class Post {

    private String title;
    private String subtitle;
    private Double price;
    private int ups;

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

    public int getImage(int i){
        switch (i){
            case 0:
                return (R.drawable.image_test_1);

            case 1:
                return (R.drawable.image_test_2);

            case 2:
                return (R.drawable.image_test_3);

            default:
                return (R.drawable.default_img);
        }
    }
}
