package com.fields.curiumx.fields;

public class ChatMap {

    private String text;
    private String sender;
    private String senderUid;

    public ChatMap(){

    }

    public ChatMap(String text, String sender, String senderUid){
        this.sender = sender;
        this.text = text;
        this.senderUid = senderUid;

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
}
