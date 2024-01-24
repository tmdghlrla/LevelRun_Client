package com.qooke.levelrunproject.model;

public class MyAppUser {
    public String nickName;
    public String email;
    public String password;

    public MyAppUser() {
    }

    public MyAppUser(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }

    public MyAppUser(String nickName, String email, String password) {
        this.nickName = nickName;
        this.email = email;
        this.password = password;
    }
}