package com.istef.OutdoorActivity2024.model;

import java.time.LocalDateTime;

public class ForecastHour {
    private LocalDateTime localDateTime;
    private DayTime dayTime;
    private WeekDay weekDay;
    private UnitValue<Float> temperature;
    private UnitValue<Float> windSpeed;
    private UnitValue<Float> windGustSpeed;
    private UnitValue<Float> atmPressure;
    private UnitValue<Integer> visibility;
    private int rainChance;
    private int clouds;
    private int humidity;
    private int snowChance;
    private float snow;

    public ForecastHour() {
    }

    /**
     * @param temperature    dual value - in metric (°C) and imperial (°F) units
     * @param windSpeed,     dual value - in metric (km/h) and imperial (miles/h) units
     * @param windGustSpeed, dual value - in metric (km/h) and imperial (miles/h) units
     * @param atmPressure,   dual value - in metric (hecto Pascals) and imperial (inches of mercury) units
     * @param visibility,    dual value - in metric (meters) and imperial (feet) units
     * @param rainChance,    as percentage
     * @param clouds,        cloud cover as percentage
     * @param humidity,      as percentage
     * @param snowChance,    as percentage
     * @param snow,          snowfall in centimeters
     */
    public ForecastHour(LocalDateTime localDateTime, DayTime dayTime, WeekDay weekDay, UnitValue<Float> temperature, UnitValue<Float> windSpeed, UnitValue<Float> windGustSpeed, UnitValue<Float> atmPressure, UnitValue<Integer> visibility, int rainChance, int clouds, int snowChance, float snow, int humidity) {
        this.localDateTime = localDateTime;
        this.dayTime = dayTime;
        this.weekDay = weekDay;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windGustSpeed = windGustSpeed;
        this.atmPressure = atmPressure;
        this.visibility = visibility;
        this.rainChance = rainChance;
        this.clouds = clouds;
        this.humidity = humidity;
        this.snowChance = snowChance;
        this.snow = snow;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public DayTime getDayTime() {
        return dayTime;
    }

    public WeekDay getWeekDay() {
        return weekDay;
    }

    public float getTemperature(Units units) {
        return temperature.get(units);
    }

    public float getWindSpeed(Units units) {
        return windSpeed.get(units);
    }

    public float getWindGustSpeed(Units units) {
        return windGustSpeed.get(units);
    }

    public float getAtmPressure(Units units) {
        return atmPressure.get(units);
    }

    public int getVisibility(Units units) {
        return visibility.get(units);
    }

    public int getRainChance() {
        return rainChance;
    }

    public int getClouds() {
        return clouds;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getSnowChance() {
        return snowChance;
    }

    public float getSnow() {
        return snow;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void setDayTime(DayTime dayTime) {
        this.dayTime = dayTime;
    }

    public void setWeekDay(WeekDay weekDay) {
        this.weekDay = weekDay;
    }

    public void setTemperature(float temperature, Units units) {
        if (this.temperature == null) this.temperature = new UnitValue<>();
        this.temperature.set(temperature, units);
    }

    public void setWindSpeed(float windSpeed, Units units) {
        if (this.windSpeed == null) this.windSpeed = new UnitValue<>();
        this.windSpeed.set(windSpeed, units);
    }

    public void setWindGustSpeed(float windGustSpeed, Units units) {
        if (this.windGustSpeed == null) this.windGustSpeed = new UnitValue<>();
        this.windGustSpeed.set(windGustSpeed, units);
    }

    public void setAtmPressure(float atmPressure, Units units) {
        if (this.atmPressure == null) this.atmPressure = new UnitValue<>();
        this.atmPressure.set(atmPressure, units);
    }

    public void setVisibility(int visibility, Units units) {
        if (this.visibility == null) this.visibility = new UnitValue<>();
        this.visibility.set(visibility, units);
    }

    public void setRainChance(int rainChance) {
        this.rainChance = rainChance;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setSnowChance(int snowChance) {
        this.snowChance = snowChance;
    }

    public void setSnow(float snow) {
        this.snow = snow;
    }

    @Override
    public String toString() {
        return "ForecastHour{" +
               "localDateTime=" + localDateTime +
               ", dayTime=" + dayTime +
               ", weekDay=" + weekDay +
               ", temperature=" + temperature +
               ", windSpeed=" + windSpeed +
               ", windGustSpeed=" + windGustSpeed +
               ", atmPressure=" + atmPressure +
               ", visibility=" + visibility +
               ", rainChance=" + rainChance +
               ", clouds=" + clouds +
               ", humidity=" + humidity +
               ", snowChance=" + snowChance +
               ", snow=" + snow +
               '}';
    }
}
