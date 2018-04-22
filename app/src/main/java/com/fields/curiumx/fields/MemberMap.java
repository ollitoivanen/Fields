package com.fields.curiumx.fields;

public class MemberMap {
    String usernameMember;
    String uidMember;

    public MemberMap(){

    }

    public MemberMap(String usernameMember, String uidMember){
        this.usernameMember = usernameMember;
        this.uidMember = uidMember;
    }

    public String getUsernameMember() {
        return usernameMember;
    }

    public String getUidMember() {
        return uidMember;
    }
}
