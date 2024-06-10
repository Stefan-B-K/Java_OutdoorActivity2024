package com.istef.OutdoorActivity2024.model;


public enum DayTime {
    DAY, NIGHT;

    public static DayTime init(String value) {
        value = value.trim().toUpperCase();
        if (value.startsWith("D")) return DAY;
        if (value.startsWith("N")) return NIGHT;
        throw new NumberFormatException("Invalid value: " + value);
    }
}
