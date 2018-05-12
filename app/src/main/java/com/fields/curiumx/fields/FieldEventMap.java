package com.fields.curiumx.fields;

import java.util.Date;

public class FieldEventMap {
    private String fieldEventTimeStart;
    private String fieldEventTimeEnd;
    private Date fieldEventDate;
    private String fieldEventTeam;
    private int fieldEventType;

    public FieldEventMap(){

    }

    public FieldEventMap(String fieldEventTimeStart,String fieldEventTimeEnd, Date fieldEventDate, int fieldEventType, String fieldEventTeam){
        this.fieldEventTimeStart = fieldEventTimeStart;
        this.fieldEventTimeEnd = fieldEventTimeEnd;
        this.fieldEventDate = fieldEventDate;
        this.fieldEventType = fieldEventType;
        this.fieldEventTeam = fieldEventTeam;
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
}
