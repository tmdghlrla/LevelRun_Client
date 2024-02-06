package com.qooke.levelrunproject.model;

public class WeatherRes {
    public Weather weather;
    public String name;


    public class Weather {
        public int id;
        public String description;
        public String icon;
    }
}
