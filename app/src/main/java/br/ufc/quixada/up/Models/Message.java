package br.ufc.quixada.up.Models;

import java.util.Date;

import br.ufc.quixada.up.Utils.DateTimeControl;

public class Message {

    private String text;
    private String userId;
    private String dateTime;

    public Message() { }

    public Message(String text, String messageUserId) {
        this.text = text;
        this.userId = messageUserId;
        this.dateTime = DateTimeControl.getCurrentDateTime();
    }

    public String getText() {
        return text;
    }

    public void setText(String messageText) {
        this.text = messageText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}
