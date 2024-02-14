package com.qooke.levelrunproject.model;

import java.util.ArrayList;

public class UserInfoRes {
    public String result;
    public int id;
    public int rank;
    public String nickName;
    public String email;
    public String profileUrl;
    public int level;
    public int exp;
    public int userId;
    public int boxCount;
    public String createdAt;
    public ArrayList<Character> items;

    public UserInfoRes(String nickName, int level, int exp) {
        this.nickName = nickName;
        this.level = level;
        this.exp = exp;
    }
}