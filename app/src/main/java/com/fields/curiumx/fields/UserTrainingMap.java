package com.fields.curiumx.fields;

public class UserTrainingMap {
    private String fieldName;
    private String username;
    private String userID;

    public UserTrainingMap(){}

    public UserTrainingMap(String fieldName, String username, String userID){
        this.username = username;
        this.fieldName = fieldName;
        this.userID = userID;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }
}
