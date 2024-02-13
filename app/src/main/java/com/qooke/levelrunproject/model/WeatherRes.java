package com.qooke.levelrunproject.model;

import java.util.ArrayList;

public class WeatherRes {
    public ArrayList<Weather> weather;
    public Temp main;
    public String name;
    public ArrayList<Air> list;


    public class Weather {
        public int id;
        public String description;
        public String icon;
    }

    public class Temp {
        public double temp;
        public double temp_min;
        public double temp_max;
    }

    public class Air {
        public AirMain main;
        public ArrayList<Weather> weather;
        public String dt_txt;

    }
    public class AirMain {
        public int aqi;
        double temp;

    }
}
