package com.qooke.levelrunproject.model;

public class Posting {

    public int photoId;
    public String imgUrl;
    public String content;
    public int userId;
    public String email;
    public String createdAt;
    public int likeCnt;
    public int isLike;

    public Posting(int photoId, String imgUrl, String content, int userId, String email, String createdAt, int likeCnt, int isLike) {
        this.photoId = photoId;
        this.imgUrl = imgUrl;
        this.content = content;
        this.userId = userId;
        this.email = email;
        this.createdAt = createdAt;
        this.likeCnt = likeCnt;
        this.isLike = isLike;
    }
}
