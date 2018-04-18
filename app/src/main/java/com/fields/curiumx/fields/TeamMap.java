package com.fields.curiumx.fields;



public class TeamMap {
    private String teamNameText;
    private String teamCountryText;
    private String teamID;
    private String homeField;
    private String leader;
    private String workStyle;
    private String level;

    public TeamMap() {

    }

    public TeamMap(String teamNameText, String teamCountryText, String teamID, String homeField, String leader, String workStyle, String level){
        this.teamNameText = teamNameText;
        this.teamCountryText = teamCountryText;
        this.teamID = teamID;
        this.homeField = homeField;
        this.leader = leader;
        this.workStyle = workStyle;
        this.level = level;
    }

    public String getTeamNameText(){
        return teamNameText;
    }

    public String getTeamCountryText(){
        return teamCountryText;
    }

    public String getHomeField() {
        return homeField;
    }

    public String getLeader() {
        return leader;
    }

    public String getLevel() {
        return level;
    }

    public String getTeamID() {
        return teamID;
    }

    public String getWorkStyle() {
        return workStyle;
    }
}
