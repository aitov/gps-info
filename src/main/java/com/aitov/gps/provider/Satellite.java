package com.aitov.gps.provider;

/**
 * Satellite information
 */
public class Satellite {
    private String id;
    private int elevation;
    private int azimuth;
    private int noise;

    public Satellite(String id, int elevation, int azimuth, int noise) {
        this.id = id;
        this.elevation = elevation;
        this.azimuth = azimuth;
        this.noise = noise;
    }

    public String getId() {
        return id;
    }

    public int getElevation() {
        return elevation;
    }

    public int getAzimuth() {
        return azimuth;
    }

    public int getNoise() {
        return noise;
    }

    @Override
    public String toString() {
        return "Satellite{" +
                "id='" + id + '\'' +
                ", elevation=" + elevation +
                ", azimuth=" + azimuth +
                ", noise=" + noise +
                '}';
    }
}
