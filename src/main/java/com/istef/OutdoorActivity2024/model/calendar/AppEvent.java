package com.istef.OutdoorActivity2024.model.calendar;

import com.istef.OutdoorActivity2024.model.HourInterval;
import com.istef.OutdoorActivity2024.model.io.ConditionsType;

public class AppEvent {
    private String eventId;
    private String location;
    private String activity;
    private ConditionsType conditionsType;
    private HourInterval hourInterval;

    public AppEvent(String eventId, String location, String activity, ConditionsType conditionsType, HourInterval hourInterval) {
        this.eventId = eventId;
        this.location = location;
        this.activity = activity;
        this.conditionsType = conditionsType;
        this.hourInterval = hourInterval;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public ConditionsType getConditionsType() {
        return conditionsType;
    }

    public void setConditionsType(ConditionsType conditionsType) {
        this.conditionsType = conditionsType;
    }

    public HourInterval getHourInterval() {
        return hourInterval;
    }

    public void setHourInterval(HourInterval hourInterval) {
        this.hourInterval = hourInterval;
    }
}
