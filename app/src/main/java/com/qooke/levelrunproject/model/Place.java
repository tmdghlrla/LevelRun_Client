package com.qooke.levelrunproject.model;

import java.io.Serializable;

public class Place {
    public String name;
    public String vicinity;
    public Geometry geometry;

    public class Geometry {
        public Location location;

        public class Location {
            public double lat;
            public double lng;
        }
    }
}