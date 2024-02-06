package com.qooke.levelrunproject.model;

import java.io.Serializable;

public class Ranker implements Serializable {

    public String nickName;
    public String profileUrl;
    public int level;
    public int exp;
    public int ranking;


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
