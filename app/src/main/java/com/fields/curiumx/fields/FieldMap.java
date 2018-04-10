package com.fields.curiumx.fields;

import java.lang.ref.SoftReference;

public class FieldMap {
    private String fieldName;
    private String fieldArea;
    private String fieldAddress;
    private String fieldID;
    private String goalCount;
    private String fieldType;
    private String accessType;
    private String creator;



    public FieldMap(){

    }

    public FieldMap(String fieldName, String fieldArea, String fieldAddress, String fieldID, String goalCount, String fieldType, String accessType, String creator){
        this.fieldName = fieldName;
        this.fieldArea = fieldArea;
        this.fieldAddress = fieldAddress;
        this.fieldID = fieldID;
        this.goalCount = goalCount;
        this.fieldType = fieldType;
        this.accessType = accessType;
        this.creator = creator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getFieldAddress() {
        return fieldAddress;
    }

    public String getFieldArea() {
        return fieldArea;
    }

    public String getFieldID() {
        return fieldID;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getGoalCount() {
        return goalCount;
    }

    public String getCreator() {
        return creator;
    }
}
