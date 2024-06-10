package com.istef.OutdoorActivity2024.service.calendar.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.ExtendedProperties;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.istef.OutdoorActivity2024.exceptios.CalendarException;
import com.istef.OutdoorActivity2024.model.HourInterval;
import com.istef.OutdoorActivity2024.model.calendar.AppEvent;
import com.istef.OutdoorActivity2024.model.io.ConditionsType;
import com.istef.OutdoorActivity2024.service.calendar.CalendarService;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * CalendarService implementation via the <a href="https://developers.google.com/calendar/api">Google Calendar API</a>
 *
 * @see CalendarService
 */
public class CalendarServiceGoogle implements CalendarService {

    /**
     * Pick from <a href="https://google-calendar-simple-api.readthedocs.io/en/latest/colors.html#id2">Event colors</a>
     */
    private static final String COLOR_ID_ALL = "2";
    private static final String COLOR_ID_PREF = "10";
    private static final ZoneId TIME_ZONE = TimeZone.getDefault().toZoneId();

    private static final String ACTIVITY_ID = "activity";
    private static final String CONDITION_TYPE_ID = "conditionType";

    private Calendar client;
    private String calendarId;
    private String userEmail;

    /**
     * Method to be called as first statement in each public method of this service ! ! !
     * <p>
     * (in order to avoid http calls in the constructor body )
     */
    private void beforeEach() throws CalendarException {
        if (this.client == null) this.client = CalendarClient.getClient();
        if (this.calendarId == null) this.calendarId = CalendarConfig.getCalendarId();

        if (this.userEmail != null) return;

        try {
            this.userEmail = client.calendars().get("primary").execute().getId();
        } catch (IOException e) {
            throw new CalendarException(e.getMessage());
        }
    }

    public static void initialUserAuth() throws CalendarException {
        CalendarClient.initialUserAuth();
    }

    @Override
    public List<AppEvent> getEvents() throws CalendarException {
        beforeEach();

        List<AppEvent> appEvents = new ArrayList<>();

        try {
            String pageToken = null;
            do {
                Events events = client.events()
                        .list(calendarId)
                        .setPageToken(pageToken)
                        .execute();

                events.getItems().forEach(event ->
                        appEvents.add(mapCalendarToAppEvent(event)));

                pageToken = events.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException e) {
            throw new CalendarException(e.getMessage());
        }

        return appEvents;
    }

    @Override
    public String addEvent(String activity,
                           ConditionsType conditionsType,
                           HourInterval hourInterval,
                           String location) throws CalendarException {
        beforeEach();

        String label = activity + " (" + conditionsType.getDescription() + ')';
        String colorId = conditionsType == ConditionsType.ALL
                ? COLOR_ID_ALL : COLOR_ID_PREF;

        ExtendedProperties props = new Event.ExtendedProperties();
        Map<String, String> privateProps = new HashMap<>();
        privateProps.put(ACTIVITY_ID, activity);
        privateProps.put(CONDITION_TYPE_ID, conditionsType.name());
        props.setPrivate(privateProps);

        Event event = new Event()
                .setSummary(label)
                .setLocation(location)
                .setExtendedProperties(props)
                .setAttendees(List.of(
                        new EventAttendee().setEmail(aliasMail(userEmail))))
                .setColorId(colorId);

        setEventStartEnd(event, hourInterval);

        try {
            event = client.events().insert(calendarId, event)
                    .setSendUpdates("all")
                    .execute();
            return event.getId();

        } catch (IOException e) {
            throw new CalendarException(e.getMessage());
        }
    }

    @Override
    public void updateEvent(String eventId, HourInterval newHourInterval) throws CalendarException {
        beforeEach();
        try {
            Event event = client.events().get(calendarId, eventId).execute();
            setEventStartEnd(event, newHourInterval);

            client.events().update(calendarId, eventId, event)
                    .setSendUpdates("all")
                    .execute();
        } catch (IOException e) {
            throw new CalendarException(e.getMessage());
        }
    }

    @Override
    public void deleteEvent(String eventId) throws CalendarException {
        beforeEach();
        try {
            client.events().delete(calendarId, eventId)
                    .setSendUpdates("all")
                    .execute();
        } catch (IOException e) {
            throw new CalendarException(e.getMessage());
        }

    }

    private Date ldtToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(TIME_ZONE).toInstant());
    }

    private String aliasMail(String mail) {
        String[] split = mail.split("@");
        return split[0] + "+outdoor@" + split[1];
    }

    private void setEventStartEnd(Event event, HourInterval hourInterval) {
        EventDateTime start = new EventDateTime()
                .setDateTime(new DateTime(
                        ldtToDate(hourInterval.getStart())))
                .setTimeZone(TIME_ZONE.getId());
        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(
                        ldtToDate(hourInterval.getEnd().plusHours(1))))
                .setTimeZone(TIME_ZONE.getId());
        event.setStart(start);
        event.setEnd(end);
    }

    private AppEvent mapCalendarToAppEvent(Event event) {
        String eventId = event.getId();
        String location = event.getLocation();
        String activity = event
                .getExtendedProperties()
                .getPrivate()
                .get(ACTIVITY_ID);
        ConditionsType conditionsType = ConditionsType.valueOf(event
                .getExtendedProperties()
                .getPrivate()
                .get(CONDITION_TYPE_ID));

        LocalDateTime start = dateToLDT(event.getStart().getDateTime());
        LocalDateTime end = dateToLDT(event.getEnd().getDateTime()).minusHours(1);
        HourInterval hourInterval = new HourInterval(start, end);

        return new AppEvent(eventId, location, activity, conditionsType, hourInterval);
    }

    private LocalDateTime dateToLDT(DateTime date) {
        return Instant.ofEpochMilli(date.getValue())
                .atZone(TIME_ZONE)
                .toLocalDateTime();
    }
}