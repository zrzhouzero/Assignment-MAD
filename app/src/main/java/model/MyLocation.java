package model;

import java.io.Serializable;

public class MyLocation implements Serializable {

    private double latitude;
    private double longitude;

    public MyLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public String toString() {
        return String.valueOf(latitude) + ", " + String.valueOf(longitude);
    }

}
