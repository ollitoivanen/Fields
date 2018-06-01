package com.fields.curiumx.fields;

import java.util.Date;

public class ChatMap {

    private String text;
    private String sender;
    private String senderUid;
    private Date time;
    private String receiverUid;

    public ChatMap(){

    }

    public ChatMap(String text, String sender, String senderUid, Date time, String receiverUid){
        this.sender = sender;
        this.text = text;
        this.senderUid = senderUid;
        this.time = time;
        this.receiverUid = receiverUid;

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

    public String getReceiverUid() {
        return receiverUid;
    }
}
