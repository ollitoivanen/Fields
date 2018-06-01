package com.fields.curiumx.fields;

public class FriendMapTest {
    private String adder;
    private String target;
    private String token;


    public FriendMapTest(){

    }

    public FriendMapTest(String adder, String target, String token){
        this.target = target;
        this.adder = adder;
        this.token = token;
    }

    public String getAdder() {
        return adder;
    }

    public String getTarget() {
        return target;
    }

    public String getToken() {
        return token;
    }
}
