package com.istef.OutdoorActivity2024.model.io;

import com.istef.OutdoorActivity2024.model.HourInterval;

import java.time.LocalDate;
import java.util.List;

/**
 * For printing the suitable hours per day
 */
public class PrintHours {
    private LocalDate date;
    private List<HourInterval> hours;

    public PrintHours(LocalDate date, List<HourInterval> hours) {
        this.date = date;
        this.hours = hours;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<HourInterval> getHours() {
        return hours;
    }

    public void setHours(List<HourInterval> hours) {
        this.hours = hours;
    }

    @Override
    public String toString() {
        return "{\n" +
               "date: \"" + date + "\"\n" +
               "hours: " + hours + '\n' +
               '}';
    }
}
