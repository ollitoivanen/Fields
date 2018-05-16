package com.fields.curiumx.fields;

import java.util.Date;

public class TrainingMap {

    private Date trainingDate;
    private long reputation;
    private long duration;
    private String field;

    public TrainingMap(){

    }

    public TrainingMap(Date trainingDate, long reputation, long duration, String field){
        this.trainingDate = trainingDate;
        this.reputation = reputation;
        this.duration = duration;
        this.field = field;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public long getDuration() {
        return duration;
    }

    public long getReputation() {
        return reputation;
    }

    public String getField() {
        return field;
    }
}
