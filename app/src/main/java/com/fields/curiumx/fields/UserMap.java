package com.fields.curiumx.fields;



public class UserMap {
    private String displayName;
    private String username;
    private String userID;
    private String currentFieldID;
    private String currentFieldName;
    private String usersTeam;
    private String userRole;
    private int userReputation;
    private String userBio;
    private String position;
    private String profilePic;

    public  UserMap(){

    }
    public UserMap(String username, String userID, String currentFieldID, String currentFieldName, String usersTeam, String displayName, String userRole, int userReputation, String userBio, String position, String profilePic){
        this.username = username;
        this.userID = userID;
        this.currentFieldID = currentFieldID;
        this.currentFieldName = currentFieldName;
        this.usersTeam = usersTeam;
        this.displayName = displayName;
        this.userRole = userRole;
        this.userReputation = userReputation;
        this.userBio = userBio;
        this.position = position;
        this.profilePic = profilePic;
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

    public String getUserRole() {
        return userRole;
    }

    public int getUserReputation() {
        return userReputation;
    }

    public String getUserBio() {
        return userBio;
    }

    public String getPosition() {
        return position;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
