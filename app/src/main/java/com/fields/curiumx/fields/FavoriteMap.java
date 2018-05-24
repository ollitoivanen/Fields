package com.fields.curiumx.fields;

public class FavoriteMap {

    private String fieldID;
    private String fieldName;

    public FavoriteMap(){

    }

    public FavoriteMap(String fieldID, String fieldName){
        this.fieldID = fieldID;
        this.fieldName = fieldName;
    }

    public String getFieldID() {
        return fieldID;
    }

    public String getFieldName() {
        return fieldName;
    }
}
