package com.istef.OutdoorActivity2024.model.openWeatherMap;

import com.istef.OutdoorActivity2024.exceptios.ForecastException;

public interface ResponseHandler<T> {
    T handleResponse(String responseBody) throws ForecastException;
}
