package com.fields.curiumx.fields;

import java.util.Date;

public class EventMap {
     String eventID;
     int eventType;
     String eventTimeStart;
     String eventTimeEnd;
     String eventField;
     Date eventStartDate;
     long eventStartDateInMillis;

    public EventMap(){

    }

    public EventMap(String eventID, int eventType, String eventTimeStart, String eventTimeEnd, String eventField, Date eventStartDate, Long eventStartDateInMillis){
        this.eventID = eventID;
        this.eventType = eventType;
        this.eventTimeStart = eventTimeStart;
        this.eventField = eventField;
        this.eventTimeEnd = eventTimeEnd;
        this.eventStartDate = eventStartDate;
        this.eventStartDateInMillis = eventStartDateInMillis;
    }

    public String getEventField() {
        return eventField;
    }

    public String getEventID() {
        return eventID;
    }

    public String getEventTimeStart() {
        return eventTimeStart;
    }

    public int getEventType() {
        return eventType;
    }

    public String getEventTimeEnd() {
        return eventTimeEnd;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public long getEventStartDateInMillis() {
        return eventStartDateInMillis;
    }
}
