package com.fields.curiumx.fields;

public class PendingMap {
    String userID;
    String userNamePending;
    Boolean pendingFieldsPlus;

    public  PendingMap(){

    }

    public  PendingMap(String userID, String userNamePending, Boolean pendingFieldsPlus){
        this.userID = userID;
        this.userNamePending = userNamePending;
        this.pendingFieldsPlus = pendingFieldsPlus;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserNamePending() {
        return userNamePending;
    }

    public Boolean getPendingFieldsPlus() {
        return pendingFieldsPlus;
    }
}
