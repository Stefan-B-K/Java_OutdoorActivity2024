package com.istef.OutdoorActivity2024.service.forecast;

import com.istef.OutdoorActivity2024.exceptios.ForecastException;
import com.istef.OutdoorActivity2024.model.Forecast;

/**
 * Definition of weather service that provides Forecast
 *
 * @see Forecast
 */
public interface ForecastService {

    /**
     * Get weather forecast
     *
     * @param city      the location (town or city)
     * @param daysAhead the number of days to get forecast for
     * @return forecast data consolidated in a Forecast type
     * @see Forecast
     */
    Forecast getForecast(String city, int daysAhead) throws ForecastException;
}
