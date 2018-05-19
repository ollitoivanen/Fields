package com.fields.curiumx.fields;

public class FieldMap {
    private String fieldName;
    private  String fieldNameLowerCase;
    private String fieldArea;
    private String fieldAreaLowerCase;
    private String fieldAddress;
    private String fieldID;
    private int goalCount;
    private int fieldType;
    private int accessType;




    public FieldMap(){

    }

    public FieldMap(String fieldName, String fieldNameLowerCase, String fieldArea, String fieldAreaLowerCase, String fieldAddress,
                    String fieldID, int goalCount, int fieldType, int accessType){
        this.fieldName = fieldName;
        this.fieldArea = fieldArea;
        this.fieldAddress = fieldAddress;
        this.fieldID = fieldID;
        this.goalCount = goalCount;
        this.fieldType = fieldType;
        this.accessType = accessType;
        this.fieldNameLowerCase = fieldNameLowerCase;
        this.fieldAreaLowerCase = fieldAreaLowerCase;

    }

    public String getFieldName() {
        return fieldName;
    }

    public int getAccessType() {
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

    public int getFieldType() {
        return fieldType;
    }

    public int getGoalCount() {
        return goalCount;
    }

    public String getFieldNameLowerCase() {
        return fieldNameLowerCase;

    }

    public String getFieldAreaLowerCase() {
        return fieldAreaLowerCase;
    }
}
