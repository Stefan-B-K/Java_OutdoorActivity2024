package com.istef.OutdoorActivity2024.model.io;

import com.istef.OutdoorActivity2024.model.Units;

import java.util.List;


public class InputData {
    private final String city;
    private final int daysAhead;
    private final Units units;
    private final int minConsecutiveHours;
    private final List<InputActivity> inputActivities;


    public InputData(String city, int daysAhead, Units units, int minConsecutiveHours, List<InputActivity> inputActivities) {
        this.city = city;
        this.daysAhead = daysAhead;
        this.units = units;
        this.minConsecutiveHours = minConsecutiveHours;
        this.inputActivities = inputActivities;
    }

    public String getCity() {
        return city;
    }

    public int getDaysAhead() {
        return daysAhead;
    }

    public Units getUnits() {
        return units;
    }

    public int getMinConsecutiveHours() {
        return minConsecutiveHours;
    }

    public List<InputActivity> getInputActivities() {
        return inputActivities;
    }

}
