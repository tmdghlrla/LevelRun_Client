package com.qooke.levelrunproject.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Ranker implements Serializable {

    public int id;
    public String nickName;
    public String profileUrl;
    public int level;
    public int exp;
    public int ranking;
    public String imgURL;
    public String content;
    public String createdAt;

    
    // 밑에 멤버변수 3개 추가
    public String result;
    public ArrayList<UserInfoRes> items;
    public int myRank;


    public Ranker() {

    }

    public Ranker(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public Ranker(String nickName, String profileUrl, int level, int ranking) {
        this.nickName = nickName;
        this.profileUrl = profileUrl;
        this.level = level;
        this.ranking = ranking;
    }
}
