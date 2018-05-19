package com.fields.curiumx.fields;

public class FriendMap {
    String userName;
    String userID;

    public FriendMap(){

    }

    public FriendMap(String userName, String userID){
        this.userName = userName;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

}
