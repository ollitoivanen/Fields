package com.fields.curiumx.fields;



public class TeamMap {
    private String teamNameText;
    private String teamCountryText;

    public TeamMap() {

    }

    public TeamMap(String teamNameText, String teamCountryText){
        this.teamNameText = teamNameText;
        this.teamCountryText = teamCountryText;
    }

    public String getTeamNameText(){
        return teamNameText;
    }



    public String getTeamCountryText(){
        return teamCountryText;
    }


}
