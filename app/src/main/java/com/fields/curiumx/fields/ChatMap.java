package com.fields.curiumx.fields;

import java.util.Date;

public class ChatMap {

    private String text;
    private String sender;
    private String senderUid;
    private Date time;

    public ChatMap(){

    }

    public ChatMap(String text, String sender, String senderUid, Date time){
        this.sender = sender;
        this.text = text;
        this.senderUid = senderUid;
        this.time = time;

    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public Date getTime() {
        return time;
    }
}
