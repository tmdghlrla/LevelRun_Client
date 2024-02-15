package com.qooke.levelrunproject.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {
    public int postingId;
    public String profileUrl;
    public String nickName;
    public int level;
    public String postingUrl;
    public String content;
    public String createdAt;
    public ArrayList<String> likerList;

}
