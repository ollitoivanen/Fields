package com.fields.curiumx.fields;

import java.util.Date;

public class FieldEventMap {
    private String fieldEventTimeStart;
    private String fieldEventTimeEnd;
    private Date fieldEventDate;
    private String fieldEventTeam;
    private int fieldEventType;
    private long eventStartDateMillis;
    private String teamID;
    private String eventID;

    public FieldEventMap(){

    }

    public FieldEventMap(String fieldEventTimeStart,String fieldEventTimeEnd, Date fieldEventDate, int fieldEventType, String fieldEventTeam, long eventStartDateMillis, String teamID, String eventID){
        this.fieldEventTimeStart = fieldEventTimeStart;
        this.fieldEventTimeEnd = fieldEventTimeEnd;
        this.fieldEventDate = fieldEventDate;
        this.fieldEventType = fieldEventType;
        this.fieldEventTeam = fieldEventTeam;
        this.eventStartDateMillis = eventStartDateMillis;
        this.teamID = teamID;
        this.eventID = eventID;
    }

    public Date getFieldEventDate() {
        return fieldEventDate;
    }

    public String getFieldEventTeam() {
        return fieldEventTeam;
    }

    public String getFieldEventTimeStart() {
        return fieldEventTimeStart;
    }

    public int getFieldEventType() {
        return fieldEventType;
    }

    public String getFieldEventTimeEnd() {
        return fieldEventTimeEnd;
    }

    public long getEventStartDateMillis() {
        return eventStartDateMillis;
    }

    public String getTeamID() {
        return teamID;
    }

    public String getEventID() {
        return eventID;
    }
}
