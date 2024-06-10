package com.istef.OutdoorActivity2024.service.calendar.google;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.istef.OutdoorActivity2024.OutdoorActivityApp;
import com.istef.OutdoorActivity2024.exceptios.CalendarException;
import com.istef.OutdoorActivity2024.exceptios.OutputException;
import com.istef.OutdoorActivity2024.model.calendar.AppCalendar;
import com.istef.OutdoorActivity2024.model.io.StoreKind;
import com.istef.OutdoorActivity2024.service.persistence.FilePersistService;
import com.istef.OutdoorActivity2024.service.persistence.PersistenceService;

import java.io.IOException;
import java.util.TimeZone;


class CalendarConfig {

    /**
     * Pick from <a href="https://google-calendar-simple-api.readthedocs.io/en/latest/colors.html#id3">Calendar colors</a>
     */
    private static final String COLOR_ID = "7";

    private static final PersistenceService persistenceService =
            FilePersistService.getInstance();
    private static final com.google.api.services.calendar.Calendar client;

    static {
        try {
            client = CalendarClient.getClient();
        } catch (CalendarException e) {
            throw new RuntimeException(e);
        }
    }

    private CalendarConfig() {
    }

    static String getCalendarId() throws CalendarException {
        String calendarId;

        AppCalendar appCalendar = persistenceService
                .getData(AppCalendar.class.getSimpleName(), AppCalendar.class, StoreKind.CONFIG);

        if (appCalendar != null) {
            calendarId = appCalendar.getId();
            if (calendarExists(calendarId)) return calendarId;
        }

        try {
            calendarId = createCalendar();
            appCalendar = new AppCalendar(calendarId);
            persistenceService.saveData(appCalendar, StoreKind.CONFIG);

        } catch (OutputException | IOException e) {
            throw new CalendarException(e.getMessage());
        }

        return calendarId;
    }

    private static boolean calendarExists(String calendarId) throws CalendarException {
        try {
            String pageToken = null;
            do {
                CalendarList calendarList = client.calendarList()
                        .list()
                        .setPageToken(pageToken)
                        .execute();

                for (CalendarListEntry calendar : calendarList.getItems()) {
                    if (calendar.getId().equals(calendarId)) return true;
                }
                pageToken = calendarList.getNextPageToken();
            } while (pageToken != null);

            return false;
        } catch (IOException e) {
            throw new CalendarException(e.getMessage());
        }
    }

    private static String createCalendar() throws IOException {
        Calendar googleCalendar = new Calendar();
        googleCalendar.setSummary(OutdoorActivityApp.APP_NAME);
        googleCalendar.setTimeZone(TimeZone.getDefault().toZoneId().getId());

        googleCalendar = client.calendars()
                .insert(googleCalendar)
                .execute();

        String calendarId = googleCalendar.getId();
        CalendarListEntry calendarListEntry =
                client.calendarList().get(calendarId).execute();
        calendarListEntry.setColorId(COLOR_ID);
        client.calendarList()
                .update(calendarId, calendarListEntry).execute();
        return calendarId;
    }

}