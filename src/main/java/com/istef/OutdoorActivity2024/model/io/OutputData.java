package com.istef.OutdoorActivity2024.model.io;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class OutputData implements Comparable<OutputData> {
    private String city;
    private int minConsecutiveHours;
    private List<OutputActivity> outputActivities;

    public OutputData() {
    }

    public OutputData(String city, int minConsecutiveHours, List<OutputActivity> outputActivities) {
        this.city = city;
        this.minConsecutiveHours = minConsecutiveHours;
        this.outputActivities = outputActivities;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<OutputActivity> getOutputActivities() {
        return outputActivities;
    }

    public void setOutputActivities(List<OutputActivity> outputActivities) {
        this.outputActivities = outputActivities;
    }

    public int getMinConsecutiveHours() {
        return minConsecutiveHours;
    }


    @Override
    public int compareTo(@NotNull OutputData o) {

        for (int i = 0; i < outputActivities.size(); i++) {
            if (outputActivities.get(i).compareTo(o.getOutputActivities().get(i)) != 0) return -1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "OutputData{" +
               "city='" + city + '\'' +
               ", minConsecutiveHours=" + minConsecutiveHours +
               ", outputActivities=" + outputActivities +
               '}';
    }
}
