package br.ufc.quixada.up;

/**
 * Created by Isaac Bruno on 09/10/2017.
 */

public class Post {

    private String title;
    private String subtitle;
    private Double price;

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
}
