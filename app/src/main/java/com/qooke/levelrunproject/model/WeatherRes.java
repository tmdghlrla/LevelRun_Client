package com.qooke.levelrunproject.model;

import java.util.ArrayList;

public class WeatherRes {
    public ArrayList<Weather> weather;
    public String name;


    public class Weather {
        public int id;
        public String description;
        public String icon;
    }
}
