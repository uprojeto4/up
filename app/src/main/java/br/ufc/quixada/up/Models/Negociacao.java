package br.ufc.quixada.up.Models;

import java.util.Date;

import br.ufc.quixada.up.Utils.DateTimeControl;

/**
 * Created by Macelo on 14/11/2017.
 */

public class Negociacao {

    private int unreadMessagesCounter;
    private String title;
    private String vendorId;
    private String vendorName;
    private String adId;
    private String lastMessage;
    private String lastMessageSenderId;
    private String startDate;
    private String messagesId;
    private String status;

    public Negociacao() { }

    public Negociacao(String messagesId, String vendorId, String adId, String lastMessage, String lastMessageSenderId) {
        this.messagesId = messagesId;
        this.vendorId = vendorId;
        this.adId = adId;
        this.lastMessage = lastMessage;
        this.lastMessageSenderId = lastMessageSenderId;
        this.startDate = DateTimeControl.getCurrentDate();
        this.status = "opened";
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

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getMessagesId() {
        return messagesId;
    }

    public void setMessagesId(String chatId) {
        this.messagesId = chatId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
