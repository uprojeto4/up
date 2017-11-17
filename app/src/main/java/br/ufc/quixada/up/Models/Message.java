package br.ufc.quixada.up.Models;

import java.util.Date;

import br.ufc.quixada.up.Utils.DateTimeControl;

public class Message {

    private String messageText;
    private String messageUserId;
    private String messageDateTime;

    public Message(String messageText, String messageUserId) {
        this.messageText = messageText;
        this.messageUserId = messageUserId;
        messageDateTime = DateTimeControl.getCurrentDateTime();
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

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }

    public String getMessageTime() {
        return messageDateTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageDateTime = messageTime;
    }
}
