package com.istef.OutdoorActivity2024.model;

/**
 * Convenience class for storing a measurement value
 * in both Metric and Imperial units
 */
public class UnitValue<T> {
    private T valueMetric;
    private T valueImperial;

    public UnitValue() {}

    public UnitValue(T valueMetric, T valueImperial) {
        this.valueMetric = valueMetric;
        this.valueImperial = valueImperial;
    }

    public UnitValue(T value, Units units) {
        if (units.equals(Units.METRIC)) this.valueMetric = value;
        else this.valueImperial = value;
    }

    public T get(Units units) {
        return units == Units.METRIC ? valueMetric : valueImperial;
    }

    public void set(T value, Units units) {
        if (units.equals(Units.METRIC)) valueMetric = value;
        else valueImperial = value;
    }

}
