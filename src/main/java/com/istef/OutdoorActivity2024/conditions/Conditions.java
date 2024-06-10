package com.istef.OutdoorActivity2024.conditions;

import com.istef.OutdoorActivity2024.exceptios.InputException;
import com.istef.OutdoorActivity2024.model.DayTime;
import com.istef.OutdoorActivity2024.model.ForecastHour;
import com.istef.OutdoorActivity2024.model.Units;
import com.istef.OutdoorActivity2024.model.WeekDay;


import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum Conditions {
    DAYTIME("Daytime: (D, N)"),
    WEEKDAY("Day of the Week: (WD, WE)"),
    TEMPERATURE_MIN("min Temperature, C | F"),
    TEMPERATURE_MAX("max Temperature, C | F"),
    CLOUDS_MIN("min Cloud Cover, %"),
    CLOUDS_MAX("max Cloud Cover, %"),
    HUMIDITY_MAX("max Humidity, %"),
    RAIN_CHANCE_MAX("max Rain Chance, %"),
    SNOW_CHANCE_MAX("max Snow Chance, %"),
    WIND_MIN("min Wind Speed, km/h | mph"),
    WIND_MAX("max Wind Speed, km/h | mph"),
    WIND_GUST_MAX("max Wind Gust Speed, km/h | mph"),
    ATM_PRESSURE_MAX("max Atm Pressure, hPa | inHg"),
    SNOW_MIN("min Snowfall, cm"),
    VISIBILITY_MIN("min Visibility, m | ft");

    private final String label;

    Conditions(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Condition init(String value, Units units) throws InputException {
        try {
            switch (this) {
                case DAYTIME:
                    return initialize(hour -> hour::getDayTime, DayTime.init(value));
                case WEEKDAY:
                    return initialize(hour -> hour::getWeekDay, WeekDay.init(value));
                case TEMPERATURE_MIN:
                case TEMPERATURE_MAX:
                    return initialize(hour -> () -> hour.getTemperature(units), Float.parseFloat(value));
                case CLOUDS_MIN:
                case CLOUDS_MAX:
                    return initialize(hour -> hour::getClouds, Integer.parseInt(value));
                case HUMIDITY_MAX:
                    return initialize(hour -> hour::getHumidity, Integer.parseInt(value));
                case RAIN_CHANCE_MAX:
                    return initialize(hour -> hour::getRainChance, Integer.parseInt(value));
                case SNOW_CHANCE_MAX:
                    return initialize(hour -> hour::getSnowChance, Integer.parseInt(value));
                case WIND_MIN:
                case WIND_MAX:
                    return initialize(hour -> () -> hour.getWindSpeed(units), Float.parseFloat(value));
                case WIND_GUST_MAX:
                    return initialize(hour -> () -> hour.getWindGustSpeed(units), Float.parseFloat(value));
                case ATM_PRESSURE_MAX:
                    return initialize(hour -> () -> hour.getAtmPressure(units), Float.parseFloat(value));
                case SNOW_MIN:
                    return initialize(hour -> hour::getSnow, Float.parseFloat(value));
                case VISIBILITY_MIN:
                    return initialize(hour -> () -> hour.getVisibility(units), Integer.parseInt(value));
                default:
                    return hours -> hours;
            }
        } catch (NumberFormatException e) {
            throw new InputException("Invalid input value " + value +  " for condition " + this);
        }
    }

    private <T extends Comparable<T>> Condition initialize(HourPropGetter<T> getProp, T value) {
        return hours -> hours.stream()
                .filter(hour -> {
                    int result = getProp.getter(hour).get().compareTo(value);
                    if (this.name().toUpperCase().endsWith("MIN")) return result > 0;
                    if (this.name().toUpperCase().endsWith("MAX")) return result < 0;
                    return result == 0;
                })
                .collect(Collectors.toList());
    }

    private interface HourPropGetter<T> {
        Supplier<T> getter(ForecastHour hour);
    }

}
