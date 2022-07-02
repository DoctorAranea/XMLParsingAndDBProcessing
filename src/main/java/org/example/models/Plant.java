package org.example.models;

public class Plant {
    private String common;
    private String botanical;
    private String zone;
    private String light;
    private double price;
    private int availability;

    public String getCommon() {
        return common;
    }

    public String getBotanical() {
        return botanical;
    }

    public String getZone() {
        return zone;
    }

    public String getLight() {
        return light;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailability() {
        return availability;
    }

    public void setCommon(String common) {
        this.common = common;
    }

    public void setBotanical(String botanical) {
        this.botanical = botanical;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }
}
