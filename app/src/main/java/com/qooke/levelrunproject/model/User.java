package com.qooke.levelrunproject.model;

public class User {
    public String nickname;
    public String email;
    public String password;

    public User() {
    }

    public User(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
}

