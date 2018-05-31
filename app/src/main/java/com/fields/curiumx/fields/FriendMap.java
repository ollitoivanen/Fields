package com.fields.curiumx.fields;

public class FriendMap {
    private String userName;
    private String userID;
    private String followerName;

    public FriendMap(){

    }

    public FriendMap(String userName, String userID, String followerName){
        this.userName = userName;
        this.userID = userID;
        this.userID = userID;
        this.followerName = followerName;

    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getFollowerName() {
        return followerName;
    }
}
