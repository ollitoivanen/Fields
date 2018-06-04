package com.fields.curiumx.fields;



public class TeamMap {
    private String teamUsernameText;
    private String teamFullNameText;
    private int teamCountryText;
    private String teamID;
    private int level;
    private Boolean teamFieldsPlus;

    public TeamMap() {

    }

    public TeamMap(String teamUsernameText, String teamFullNameText, int teamCountryText, String teamID, int level, Boolean teamFieldsPlus){
        this.teamUsernameText = teamUsernameText;
        this.teamCountryText = teamCountryText;
        this.teamID = teamID;
        this.teamFullNameText = teamFullNameText;
        this.level = level;
        this.teamFieldsPlus = teamFieldsPlus;
    }

    public String getTeamUsernameText() {
        return teamUsernameText;
    }

    public int getTeamCountryText(){
        return teamCountryText;
    }

    public int getLevel() {
        return level;
    }


    public String getTeamID() {
        return teamID;
    }

    public String getTeamFullNameText() {
        return teamFullNameText;
    }

    public Boolean getTeamFieldsPlus() {
        return teamFieldsPlus;
    }
}
