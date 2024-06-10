package com.istef.OutdoorActivity2024.model.openWeatherMap;

import com.istef.OutdoorActivity2024.exceptios.ForecastException;
import org.json.JSONArray;
import org.json.JSONObject;

public class LocationResponseHandler implements ResponseHandler<CityLocation>{

    @Override
    public CityLocation handleResponse(String responseBody) throws ForecastException {
        JSONArray jsonResponse = new JSONArray(responseBody);
        if (jsonResponse.isEmpty())
            throw new ForecastException("No matching location found.");

        JSONObject location = (JSONObject) jsonResponse.get(0);
        return new CityLocation(
                location.getString("name"),
                location.getDouble("lat"),
                location.getDouble("lon"));
    }
}
