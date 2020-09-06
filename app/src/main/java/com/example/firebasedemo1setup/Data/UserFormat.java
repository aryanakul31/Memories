package com.example.firebasedemo1setup.Data;

import java.io.Serializable;

public class UserFormat implements Serializable {
    private String userName="", userEmail="", uid="";

    public UserFormat(){
    }

    public UserFormat(String userName, String userEmail, String uid){
        this.userName = userName;
        this.userEmail = userEmail;
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
