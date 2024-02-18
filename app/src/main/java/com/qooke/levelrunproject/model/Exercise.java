package com.qooke.levelrunproject.model;

public class Exercise {
    public int id;
    public int userId;
    public double distance;
    public double kcal;
    public int seconds;
    public int steps;
    public String time;
    public String createdAt;


    public Exercise() {
    }

    public Exercise(double distance, double kcal, String time, int steps) {
        this.distance = distance;
        this.kcal = kcal;
        this.time = time;
        this.steps = steps;
    }
}
