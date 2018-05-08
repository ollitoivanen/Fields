package com.fields.curiumx.fields;

public class MemberMap {
    String usernameMember;
    String uidMember;
    Boolean memberFieldsPlus;

    public MemberMap(){

    }

    public MemberMap(String usernameMember, String uidMember, Boolean memberFieldsPlus){
        this.usernameMember = usernameMember;
        this.uidMember = uidMember;
        this.memberFieldsPlus = memberFieldsPlus;
    }

    public String getUsernameMember() {
        return usernameMember;
    }

    public String getUidMember() {
        return uidMember;
    }

    public Boolean getMemberFieldsPlus() {
        return memberFieldsPlus;
    }
}
