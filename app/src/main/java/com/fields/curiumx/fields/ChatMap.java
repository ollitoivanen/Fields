package com.fields.curiumx.fields;

public class ChatMap {

    private String text;
    private String sender;

    public ChatMap(){

    }

    public ChatMap(String text, String sender){
        this.sender = sender;
        this.text = text;

    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }
}
