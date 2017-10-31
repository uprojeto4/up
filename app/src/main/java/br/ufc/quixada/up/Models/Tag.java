package br.ufc.quixada.up.Models;

/**
 * Created by Brendon on 22/10/2017.
 */

public class Tag {
    public String title;

    public Tag(String title) {
        this.title = title;
    };

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
