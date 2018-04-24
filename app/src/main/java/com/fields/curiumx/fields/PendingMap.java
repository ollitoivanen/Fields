package com.fields.curiumx.fields;

public class PendingMap {
    String userID;
    String userNamePending;

    public  PendingMap(){

    }

    public  PendingMap(String userID, String userNamePending){
        this.userID = userID;
        this.userNamePending = userNamePending;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserNamePending() {
        return userNamePending;
    }
}
