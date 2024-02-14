package com.qooke.levelrunproject.model;

import java.io.Serial;
import java.io.Serializable;

public class MyAppUser implements Serializable {
    public int id;
    public String nickName;
    public String email;
    public String password;
    public String profileUrl;
    public int type;

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

    public MyAppUser(String nickName, String email, String password, String profileUrl) {
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.profileUrl = profileUrl;
    }

    public MyAppUser(String nickName, String email, String profileUrl, int type) {
        this.nickName = nickName;
        this.email = email;
        this.profileUrl = profileUrl;
        this.type = type;
    }
}