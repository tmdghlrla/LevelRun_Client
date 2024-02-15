package com.qooke.levelrunproject.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PostingDetail implements Serializable {
    public String result;
    public ArrayList<Post> item;
    public ArrayList<String> tagList;
    public ArrayList<String> likerList;
}
