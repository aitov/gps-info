package com.aitov.gps.provider;


import java.time.LocalDateTime;

/**
 * Position information
 */
public class Position {
    LocalDateTime date;
    private Double speed;
    private double latitude;
    private double longitude;
    private double altitude;

    public Position(LocalDateTime date, Double speed, double latitude, double longitude, double altitude) {
        this.date = date;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Double getSpeed() {
        return speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    @Override
    public String toString() {
        return "Position{" +
                "date=" + date +
                ", speed=" + speed +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }
}
