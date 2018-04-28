package com.fields.curiumx.fields;



public class TeamMap {
    private String teamNameText;
    private String teamCountryText;
    private String teamID;
    private int level;

    public TeamMap() {

    }

    public TeamMap(String teamNameText, String teamCountryText, String teamID, int level){
        this.teamNameText = teamNameText;
        this.teamCountryText = teamCountryText;
        this.teamID = teamID;

        this.level = level;
    }

    public String getTeamNameText(){
        return teamNameText;
    }

    public String getTeamCountryText(){
        return teamCountryText;
    }



    public int getLevel() {
        return level;
    }

    public String getTeamID() {
        return teamID;
    }

}
