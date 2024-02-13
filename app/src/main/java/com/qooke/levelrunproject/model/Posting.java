package com.qooke.levelrunproject.model;

import java.io.Serializable;

public class Posting implements Serializable {

    public int id;
    public String imgURL;
    public String content;
    public int userId;
    public String email;
    public String tags;
    public String createdAt;
    public String updatedAt;
    public int likeCnt;
    public int isLike;


    public Posting(int id, String imgURL, String content, int userId, String createdAt, String updatedAt) {
        this.id = id;
        this.imgURL = imgURL;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Posting() {

    }

    public Posting(String imgURL, String content, String tags) {
        this.imgURL = imgURL;
        this.content = content;
        this.tags = tags;
    }
}
