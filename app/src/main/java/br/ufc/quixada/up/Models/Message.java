package br.ufc.quixada.up.Models;

import java.util.Date;

public class Message {

    private String messageText;
    private String messageUserId;
    private long messageTime;

    public Message(String messageText, String messageUserId) {
        this.messageText = messageText;
        this.messageUserId = messageUserId;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public Message(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUser) {
        this.messageUserId = messageUserId;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
