package br.ufc.quixada.up.Models;

import java.util.Date;

/**
 * Created by Macelo on 14/11/2017.
 */

public class Negociacao {

    private String title;
    private int unreadMessagesCounter;
    private String vendor;
    private String startDate;


    public Negociacao(String title, int unreadMessagesCounter, String vendor, String startDate) {
        this.title = title;
        this.unreadMessagesCounter = unreadMessagesCounter;
        this.vendor = vendor;
        this.startDate = startDate;
    }

    public Negociacao() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnreadMessagesCounter() {
        return unreadMessagesCounter;
    }

    public void setUnreadMessagesCounter(int unreadMessagesCounter) {
        this.unreadMessagesCounter = unreadMessagesCounter;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

}
