package com.fields.curiumx.fields;


import java.util.Date;

public class UserMap {
    private String realName;
    private String username;
    private String userID;
    private String currentFieldID;
    private String currentFieldName;
    private String usersTeamID;
    private int userRole;
    private String userReputation;
    private int position;
    private int trainingCount;
    private Boolean fieldsPlus;
    private Date timestamp;
    private String token;


    public  UserMap(){

    }
    public UserMap(String username, String userID, String currentFieldID, String currentFieldName,
                   String realName, int userRole, String userReputation,
                   int position, String usersTeamID, int trainingCount, Boolean fieldsPlus, Date timestamp, String token){
        this.username = username;
        this.userID = userID;
        this.currentFieldID = currentFieldID;
        this.currentFieldName = currentFieldName;
        this.realName = realName;
        this.userRole = userRole;
        this.userReputation = userReputation;
        this.position = position;
        this.usersTeamID = usersTeamID;
        this.trainingCount = trainingCount;
        this.fieldsPlus = fieldsPlus;
        this.timestamp = timestamp;
        this.token = token;
    }

    public String getUsername(){
        return username;
    }

    public  String getUserID(){
        return userID;
    }

    public String getCurrentFieldID(){
        return currentFieldID;
    }

    public String getCurrentFieldName(){
        return currentFieldName;
    }



    public String getRealName() {
        return realName;
    }

    public int getUserRole() {
        return userRole;
    }

    public String getUserReputation() {
        return userReputation;
    }


    public int getPosition() {
        return position;
    }

    public String getUsersTeamID() {
        return usersTeamID;
    }

    public int getTrainingCount() {
        return trainingCount;
    }

    public Boolean getFieldsPlus() {
        return fieldsPlus;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getToken() {
        return token;
    }
}
