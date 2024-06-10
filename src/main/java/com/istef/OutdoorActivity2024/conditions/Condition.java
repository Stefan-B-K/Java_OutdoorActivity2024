package com.istef.OutdoorActivity2024.conditions;

import com.istef.OutdoorActivity2024.model.ForecastHour;

import java.util.List;

/**
 * Weather or temporal condition (filter), compatible
 * for use in the OutdoorActivity2024 project
 */
public interface Condition {

    /**
     * Apply condition/filter to the list of hours with forecast data.
     *
     * @param hours     the list of ForecastHours to be conditioned/filtered
     * @return          the list of ForecastHours after applying some conditions/filters
     *                  specified int the implementing class
     * @see ForecastHour
     */
    List<ForecastHour> filter(List<ForecastHour> hours);
}
