package com.istef.OutdoorActivity2024.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;


public class HourInterval implements Comparable<HourInterval>, Cloneable{
    private LocalDateTime start;
    private LocalDateTime end;

    public HourInterval() {}

    public HourInterval(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }


    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return '\"' + hourToString(start.getHour()) + '-' + hourToString(end.getHour() + 1) + '\"';
    }

    private String hourToString(int hour) {
        return hour < 10 ? "0" + hour : String.valueOf(hour);
    }


    @Override
    public int compareTo(@NotNull HourInterval o) {
        if (this.getStart().isEqual(o.getStart()) && this.getEnd().isEqual(o.getEnd())) return 0;
        if (this.getStart().isAfter(o.getStart())) return 1;
        else return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof HourInterval))
            return false;

        HourInterval otherInterval = (HourInterval) obj;

        return this.getStart().isEqual(otherInterval.getStart())
               && this.getEnd().isEqual(otherInterval.getEnd());
    }

    @Override
    public HourInterval clone() {
        try {
            return (HourInterval) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
