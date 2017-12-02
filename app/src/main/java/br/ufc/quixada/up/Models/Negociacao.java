package br.ufc.quixada.up.Models;

import java.util.Date;

import br.ufc.quixada.up.Constant;
import br.ufc.quixada.up.Utils.DateTimeControl;

/**
 * Created by Macelo on 14/11/2017.
 */

public class Negociacao {

    private int unreadMessagesCounter;
    private String title;
    private String remoteUserId;
    private String vendorName;
    private String adId;
    private String lastMessage;
    private String lastMessageSenderId;
    private long startDate;
    private String messagesId;
    private int status;

    public Negociacao() { }

    public Negociacao(String messagesId, String remoteUserId, String adId, String lastMessage, String lastMessageSenderId) {
        this.messagesId = messagesId;
        this.remoteUserId = remoteUserId;
        this.adId = adId;
        this.lastMessage = lastMessage;
        this.lastMessageSenderId = lastMessageSenderId;
        this.startDate = DateTimeControl.getCurrentDateTime();
        this.status = Constant.OPENED_NEGOTIATION;
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

    public String getRemoteUserId() {
        return remoteUserId;
    }

    public void setRemoteUserId(String vendorId) {
        this.remoteUserId = vendorId;
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

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getMessagesId() {
        return messagesId;
    }

    public void setMessagesId(String chatId) {
        this.messagesId = chatId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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
