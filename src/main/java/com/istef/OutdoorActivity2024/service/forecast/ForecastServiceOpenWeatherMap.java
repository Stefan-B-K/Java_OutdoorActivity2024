package com.istef.OutdoorActivity2024.service.forecast;

import com.istef.OutdoorActivity2024.SecretKeys;
import com.istef.OutdoorActivity2024.exceptios.ForecastException;
import com.istef.OutdoorActivity2024.model.Forecast;
import com.istef.OutdoorActivity2024.model.Units;
import com.istef.OutdoorActivity2024.model.openWeatherMap.CityLocation;
import com.istef.OutdoorActivity2024.model.openWeatherMap.LocationResponseHandler;
import com.istef.OutdoorActivity2024.model.openWeatherMap.ResponseHandler;
import com.istef.OutdoorActivity2024.model.openWeatherMap.WeatherResponseHandler;
import okhttp3.HttpUrl;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


/**
 * ForecastService implementation via the <a href="https://openweathermap.org">openweathermap.org API</a>
 * <p>
 * Hourly forecast for 48 hours !!!
 */
public class ForecastServiceOpenWeatherMap implements ForecastService {
    private final HttpClient http;
    private Forecast forecast;

    /**
     * ForecastService implementation via the <a href="https://openweathermap.org">openweathermap.org API</a>
     * <p>
     * Hourly forecast for 48 hours !!!
     */
    public ForecastServiceOpenWeatherMap() {
        this.http = HttpClient.newBuilder().build();
        this.forecast = new Forecast();
    }

    @Override
    public Forecast getForecast(String city, int daysAhead) throws ForecastException {

        CityLocation cityLocation = fetchLocation(city);

        forecast.setCity(cityLocation.getName());

        forecast = fetchForecast(cityLocation, Units.METRIC);
        forecast = fetchForecast(cityLocation, Units.IMPERIAL);

        return forecast;
    }

    private CityLocation fetchLocation(String city) throws ForecastException {

        URI uri = new HttpUrl.Builder()
                .scheme("https")
                .host("api.openweathermap.org")
                .addPathSegment("geo")
                .addPathSegment("1.0")
                .addPathSegment("direct")
                .addQueryParameter("q", city)
                .addQueryParameter("appid", SecretKeys.OPEN_WEATHER_MAP_API_KEY)
                .build().uri();

        return fetchData(uri, new LocationResponseHandler());
    }

    private Forecast fetchForecast(CityLocation location, Units units) throws ForecastException {

        URI uri = new HttpUrl.Builder()
                .scheme("https")
                .host("api.openweathermap.org")
                .addPathSegment("data")
                .addPathSegment("3.0")
                .addPathSegment("onecall")
                .addQueryParameter("lat", String.valueOf(location.getLatitude()))
                .addQueryParameter("lon", String.valueOf(location.getLongitude()))
                .addQueryParameter("units", units.name().toLowerCase())
                .addQueryParameter("appid", SecretKeys.OPEN_WEATHER_MAP_API_KEY)
                .build().uri();

        forecast = fetchData(uri, new WeatherResponseHandler(units, forecast));
        return forecast;
    }

    private <T> T fetchData(URI uri, ResponseHandler<T> responseHandler) throws ForecastException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .GET().build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().trim().isEmpty()) {
                throw new ForecastException("Weather API error: " + response.statusCode());
            }

            if (!String.valueOf(response.statusCode()).startsWith("2")) {
                String errorMessage = new JSONObject(response.body()).getString("message");
                throw new ForecastException("Weather API error: " + errorMessage);
            }

            return responseHandler.handleResponse(response.body());

        } catch (ConnectException e) {
            System.out.println(e.getMessage());
            throw new ForecastException("Connection error!");
        } catch (IOException | InterruptedException e) {
            throw new ForecastException(e.getMessage());
        }
    }

}
