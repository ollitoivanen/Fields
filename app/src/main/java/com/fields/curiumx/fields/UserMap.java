package com.fields.curiumx.fields;



public class UserMap {
    private String displayName;
    private String username;
    private String userID;
    private String currentFieldID;
    private String currentFieldName;
    private String usersTeam;
    private String usersTeamID;
    private int userRole;
    private String userReputation;
    private int position;
    private int trainingCount;


    public  UserMap(){

    }
    public UserMap(String username, String userID, String currentFieldID, String currentFieldName,
                   String usersTeam, String displayName, int userRole, String userReputation,
                   int position, String usersTeamID, int trainingCount){
        this.username = username;
        this.userID = userID;
        this.currentFieldID = currentFieldID;
        this.currentFieldName = currentFieldName;
        this.usersTeam = usersTeam;
        this.displayName = displayName;
        this.userRole = userRole;
        this.userReputation = userReputation;
        this.position = position;
        this.usersTeamID = usersTeamID;
        this.trainingCount = trainingCount;
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

    public String getUsersTeam(){
        return usersTeam;
    }

    public String getDisplayName(){
        return displayName;
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
}
