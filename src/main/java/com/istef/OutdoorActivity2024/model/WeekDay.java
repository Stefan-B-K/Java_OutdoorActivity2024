package com.istef.OutdoorActivity2024.model;

import java.time.Instant;
import java.time.ZoneId;

public enum WeekDay {
    WEEKDAY, WEEKEND;


    /**
     * Initialize from string value.
     *
     * @param value     "WD" or "WE"
     * @return          WEEKDAY or WEEKEND
     * @see ForecastHour
     */
    public static WeekDay init(String value) {
        value = value.trim().toUpperCase();
        if (value.startsWith("WD")) return WEEKDAY;
        if (value.startsWith("WE")) return WEEKEND;
        throw new NumberFormatException("Invalid value: " + value);
    }

    /**
     * Initialize from Instant and time zone id.
     *
     * @param instant     an instant (point in time)
     * @param zoneId      time zone id
     * @return            WEEKDAY or WEEKEND
     * @see Instant
     * @see ZoneId
     * @see ForecastHour
     */
    public static WeekDay from(Instant instant, ZoneId zoneId) {
        return instant.atZone(zoneId).getDayOfWeek().getValue() > 5
                ? WeekDay.WEEKEND
                : WeekDay.WEEKDAY;
    }
}
