package com.fields.curiumx.fields;

public class EventMap {
     String eventID;
     int eventType;
     String eventTimeStart;
     String eventTimeEnd;
     String eventField;
     String eventStartDate;

    public EventMap(){

    }

    public EventMap(String eventID, int eventType, String eventTimeStart, String eventTimeEnd, String eventField, String eventStartDate){
        this.eventID = eventID;
        this.eventType = eventType;
        this.eventTimeStart = eventTimeStart;
        this.eventField = eventField;
        this.eventTimeEnd = eventTimeEnd;
        this.eventStartDate = eventStartDate;
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

    public String getEventStartDate() {
        return eventStartDate;
    }
}
