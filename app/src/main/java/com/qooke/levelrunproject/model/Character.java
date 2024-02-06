package com.qooke.levelrunproject.model;

import java.io.Serializable;

public class Character implements Serializable {
    public int id;
    public int userId;
    public int characterId;
    public String createdAt;
    public String imgUrl;

    public Character() {
    }
}