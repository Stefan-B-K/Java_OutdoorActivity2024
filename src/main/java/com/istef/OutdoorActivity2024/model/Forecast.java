package com.istef.OutdoorActivity2024.model;

import java.time.ZoneId;
import java.util.List;

public class Forecast {
    private String city;
    private ZoneId timezoneId;
    private List<ForecastHour> forecastHours;

    public Forecast() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ZoneId getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(String timezoneId) {
        this.timezoneId = ZoneId.of(timezoneId);

    }

    public List<ForecastHour> getForecastHours() {
        return forecastHours;
    }

    public void setForecastHours(List<ForecastHour> forecastHours) {
        this.forecastHours = forecastHours;
    }

    @Override
    public String toString() {
        return "Forecast{" +
               "city='" + city + '\'' +
               ", timezoneId=" + timezoneId +
               ", forecastHours=" + forecastHours +
               '}';
    }
}
