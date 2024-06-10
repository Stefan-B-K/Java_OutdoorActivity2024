package com.istef.OutdoorActivity2024.service.forecast;

import com.istef.OutdoorActivity2024.SecretKeys;
import com.istef.OutdoorActivity2024.exceptios.ForecastException;
import com.istef.OutdoorActivity2024.model.*;
import okhttp3.HttpUrl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * ForecastService implementation via the <a href="https://www.weatherapi.com">weatherapi.com API</a>
 */
public class ForecastServiceWeatherApi implements ForecastService {
    private static long epochSecondsNow;
    private final Forecast forecast;

    /**
     * ForecastService implementation via the <a href="https://www.weatherapi.com">weatherapi.com API</a>
     */
    public ForecastServiceWeatherApi() {
        forecast = new Forecast();
    }

    @Override
    public Forecast getForecast(String city, int daysAhead) throws ForecastException {
        epochSecondsNow = Instant.now().toEpochMilli() / 1000;

        URI uri = new HttpUrl.Builder()
                .scheme("http")
                .host("api.weatherapi.com")
                .addPathSegment("v1")
                .addPathSegment("forecast.json")
                .addQueryParameter("key", SecretKeys.WEATHER_API_KEY)
                .addQueryParameter("q", city)
                .addQueryParameter("days", String.valueOf(daysAhead + 1))
                .build().uri();

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().trim().isEmpty()) {
                throw new ForecastException("Weather API error: " + response.statusCode());
            }
            JSONObject jsonResponse = new JSONObject(response.body());

            if (!String.valueOf(response.statusCode()).startsWith("2")) {
                String errorMessage = jsonResponse.getJSONObject("error").getString("message");
                throw new ForecastException("Weather API error: " + errorMessage);
            }

            JSONObject location = jsonResponse.getJSONObject("location");
            forecast.setCity(location.getString("name"));
            forecast.setTimezoneId(location.getString("tz_id"));

            JSONArray days = jsonResponse.getJSONObject("forecast").getJSONArray("forecastday");
            List<ForecastHour> forecastHours = new ArrayList<>();
            for (int i = 0; i < days.length(); i++) {
                addToForecastHours(days.getJSONObject(i), forecastHours);
            }
            forecast.setForecastHours(forecastHours);

            return forecast;

        } catch (ConnectException e) {
            System.out.println(e.getMessage());
            throw new ForecastException("Connection error!");
        } catch (IOException | InterruptedException e) {
            throw new ForecastException(e.getMessage());
        }
    }

    private void addToForecastHours(JSONObject jsonDay, List<ForecastHour> forecastHours) {
        JSONArray jsonHours = jsonDay.getJSONArray("hour");

        for (int i = 0; i < jsonHours.length(); i++) {
            JSONObject jsonHour = (JSONObject) jsonHours.get(i);

            int epochHour = jsonHour.getInt("time_epoch");
            if (epochHour > epochSecondsNow)
                forecastHours.add(mapJsonToForecastHour(jsonHour));
        }
    }

    private ForecastHour mapJsonToForecastHour(JSONObject jsonHour) {
        Instant instant = Instant.ofEpochSecond(jsonHour.getInt("time_epoch"));
        ZoneId myZoneId = TimeZone.getDefault().toZoneId();
        LocalDateTime localTime = instant.atZone(myZoneId).toLocalDateTime();

        int visibilityMeters = (int) (jsonHour.getFloat("vis_km") * 1000);
        int visibilityFeet = (int) (jsonHour.getFloat("vis_miles") * 5280);

        return new ForecastHour(
                localTime,
                jsonHour.getInt("is_day") == 1 ? DayTime.DAY : DayTime.NIGHT,
                WeekDay.from(instant, forecast.getTimezoneId()),
                new UnitValue<>(jsonHour.getFloat("temp_c"), jsonHour.getFloat("temp_f")),
                new UnitValue<>(jsonHour.getFloat("wind_kph"), jsonHour.getFloat("wind_mph")),
                new UnitValue<>(jsonHour.getFloat("gust_kph"), jsonHour.getFloat("gust_mph")),
                new UnitValue<>(jsonHour.getFloat("pressure_mb"), jsonHour.getFloat("pressure_in")),
                new UnitValue<>(visibilityMeters, visibilityFeet),
                jsonHour.getInt("chance_of_rain"),
                jsonHour.getInt("cloud"),
                jsonHour.getInt("chance_of_snow"),
                jsonHour.getFloat("snow_cm"),
                jsonHour.getInt("humidity")
        );
    }

}
