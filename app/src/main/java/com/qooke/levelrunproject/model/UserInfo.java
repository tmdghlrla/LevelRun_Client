package com.qooke.levelrunproject.model;

public class UserInfo {
    public String nickName;
    public String email;
    public String profileUrl;
    public String createdAt;
    public int level;
    public int exp;
    public int userId;
    public int boxCount;

    public UserInfo() {
    }

    public UserInfo(String nickName, String email, String profileUrl, String createdAt, int level, int exp, int userId) {
        this.nickName = nickName;
        this.email = email;
        this.profileUrl = profileUrl;
        this.createdAt = createdAt;
        this.level = level;
        this.exp = exp;
        this.userId = userId;
    }
}