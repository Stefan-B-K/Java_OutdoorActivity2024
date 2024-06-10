package com.istef.OutdoorActivity2024.model.openWeatherMap;

public class CityLocation {
    private final String name;
    private final double latitude;
    private final double longitude;

    public CityLocation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "CityLocation{" +
               "name='" + name + '\'' +
               ", latitude=" + latitude +
               ", longitude=" + longitude +
               '}';
    }
}
