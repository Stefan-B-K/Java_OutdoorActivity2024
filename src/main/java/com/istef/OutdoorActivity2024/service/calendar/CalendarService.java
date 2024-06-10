package com.istef.OutdoorActivity2024.service.calendar;


import com.istef.OutdoorActivity2024.exceptios.CalendarException;
import com.istef.OutdoorActivity2024.model.HourInterval;
import com.istef.OutdoorActivity2024.model.calendar.AppEvent;
import com.istef.OutdoorActivity2024.model.io.ConditionsType;

import java.util.List;


/**
 * Definition of service for interaction with user's Google Calendar,
 * compatible for use in the OutdoorActivity2024 project
 */
public interface CalendarService {

    /**
     * @return list of AppEvent objects, mapped from the corresponding calendar event type
     * @see AppEvent
     */
    List<AppEvent> getEvents() throws CalendarException;

    /**
     * @param activity      the outdoor activity to create event for
     * @param conditionType the type of weather/temporal conditions (suitable/preferred) for the activity
     * @param hourInterval  the temporal HourInterval (start, end) of the event
     * @param location      the name of the city/town hosting the Outdoor Activity
     * @return the ID of the created event
     * @see ConditionsType
     * @see HourInterval
     */
    String addEvent(String activity,
                    ConditionsType conditionType,
                    HourInterval hourInterval,
                    String location) throws CalendarException;

    /**
     * @param eventId         the ID of the event to be updated
     * @param newHourInterval the new temporal HourInterval (start, end) of the event
     * @see HourInterval
     */
    void updateEvent(String eventId,
                     HourInterval newHourInterval) throws CalendarException;

    /**
     * @param eventId the ID of the event to be deleted
     */
    void deleteEvent(String eventId) throws CalendarException;
}
