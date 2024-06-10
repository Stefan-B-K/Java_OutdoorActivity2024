package com.istef.OutdoorActivity2024.model.openWeatherMap;

import com.istef.OutdoorActivity2024.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class WeatherResponseHandler implements ResponseHandler<Forecast> {
    private final Units units;
    private final Forecast forecast;

    public WeatherResponseHandler(Units units, Forecast forecast) {
        this.units = units;
        this.forecast = forecast;
    }

    @Override
    public Forecast handleResponse(String responseBody) {
        JSONObject jsonResponse = new JSONObject(responseBody);

        forecast.setTimezoneId(jsonResponse.getString("timezone"));

        List<ForecastHour> forecastHours = units.equals(Units.METRIC)
                ? new ArrayList<>()
                : forecast.getForecastHours();

        JSONArray jsonHours = jsonResponse.getJSONArray("hourly");
        for (int i = 0; i < jsonHours.length(); i++) {
            JSONObject jsonHour = (JSONObject) jsonHours.get(i);
            if (units.equals(Units.METRIC)) {
                forecastHours.add(mapJsonToForecastHour(jsonHour, null));
            } else {
                mapJsonToForecastHour(jsonHour, forecastHours.get(i));
            }
        }


        forecast.setForecastHours(forecastHours);

        return forecast;
    }

    private ForecastHour mapJsonToForecastHour(JSONObject jsonHour, ForecastHour forecastHour) {

        float temperature = jsonHour.getFloat("temp");

        float windSpeed = jsonHour.getFloat("wind_speed");
        float windGustSpeed = jsonHour.getFloat("wind_gust");

        float atmPressure = jsonHour.getFloat("pressure");
        int visibility = jsonHour.getInt("visibility");

        if (units.equals(Units.IMPERIAL)) {
            atmPressure /= 33.8638F;                    //  hPa to inHg
            visibility = (int) (visibility / 0.3048);   // meters to feet
        }

        if (forecastHour == null && units.equals(Units.METRIC)) {
            forecastHour = new ForecastHour();

            Instant instant = Instant.ofEpochSecond(jsonHour.getLong("dt"));
            ZoneId myZoneId = TimeZone.getDefault().toZoneId();
            LocalDateTime localTime = instant.atZone(myZoneId).toLocalDateTime();

            JSONObject weather = (JSONObject) jsonHour.getJSONArray("weather").get(0);
            String jsonIcon = weather.getString("icon");
            DayTime dayTime = jsonIcon.endsWith("d") ? DayTime.DAY : DayTime.NIGHT;

            WeekDay weekDay = WeekDay.from(instant, forecast.getTimezoneId());

            int precipitationChance = (int) (jsonHour.getFloat("pop") * 100);
            int rainChance = temperature > 0 ? precipitationChance : 0;
            int snowChance = temperature <= 0 ? precipitationChance : 0;

            int clouds = jsonHour.getInt("clouds");
            int humidity = jsonHour.getInt("humidity");

            float snowfall;
            try {
                snowfall = jsonHour.getJSONObject("snow").getFloat("1h") / 10;
            } catch (JSONException e) {
                snowfall = 0;
            }

            windSpeed *= 3.6F;       //  m/s to km/h
            windGustSpeed *= 3.6F;   //  m/s to km/h

            forecastHour.setLocalDateTime(localTime);
            forecastHour.setDayTime(dayTime);
            forecastHour.setWeekDay(weekDay);
            forecastHour.setRainChance(rainChance);
            forecastHour.setClouds(clouds);
            forecastHour.setSnowChance(snowChance);
            forecastHour.setSnow(snowfall);
            forecastHour.setHumidity(humidity);
        }

        forecastHour.setTemperature(temperature, units);
        forecastHour.setWindSpeed(windSpeed, units);
        forecastHour.setWindGustSpeed(windGustSpeed, units);
        forecastHour.setAtmPressure(atmPressure, units);
        forecastHour.setVisibility(visibility, units);

        return forecastHour;
    }
}
