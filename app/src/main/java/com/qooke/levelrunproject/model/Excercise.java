package com.qooke.levelrunproject.model;

public class Excercise {
    public int id;
    public int userId;
    public double distance;
    public double cal;
    public String time;
    public int steps;


    public Excercise() {
    }

    public Excercise(double distance, double cal, String time, int steps) {
        this.distance = distance;
        this.cal = cal;
        this.time = time;
        this.steps = steps;
    }
}
