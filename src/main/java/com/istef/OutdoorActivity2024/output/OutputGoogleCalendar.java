package com.istef.OutdoorActivity2024.output;

import com.istef.OutdoorActivity2024.exceptios.CalendarException;
import com.istef.OutdoorActivity2024.exceptios.OutputException;
import com.istef.OutdoorActivity2024.model.HourInterval;
import com.istef.OutdoorActivity2024.model.calendar.AppCalendar;
import com.istef.OutdoorActivity2024.model.calendar.AppEvent;
import com.istef.OutdoorActivity2024.model.io.ConditionsType;
import com.istef.OutdoorActivity2024.model.io.OutputActivity;
import com.istef.OutdoorActivity2024.model.io.OutputData;
import com.istef.OutdoorActivity2024.model.io.StoreKind;
import com.istef.OutdoorActivity2024.service.calendar.google.CalendarServiceGoogle;
import com.istef.OutdoorActivity2024.service.persistence.FilePersistService;
import com.istef.OutdoorActivity2024.service.persistence.PersistenceService;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DataOutputer implementation that sends the output to the user's Google Calendar.
 *
 * @see DataOutputer
 */
public class OutputGoogleCalendar implements DataOutputer {

    private final CalendarServiceGoogle calendarService;
    private final PersistenceService persistenceService;
    private String location;
    private AppCalendar appCalendar;

    private List<AppEvent> appEvents, unchangedAppEvents;
    private List<String> activities;
    private Map<AppEvent, HourInterval> eventsToUpdate;
    private LocalDateTime now;

    /**
     * DataOutputer implementation that sends the output to the user's Google Calendar.
     *
     * @see DataOutputer
     */
    public OutputGoogleCalendar() {
        this.calendarService = new CalendarServiceGoogle();
        this.persistenceService = FilePersistService.getInstance();
    }


    @Override
    public void output(@Nullable OutputData outputData) throws OutputException {
        if (outputData == null) return;

        location = outputData.getCity();
        appCalendar = persistenceService
                .getData(AppCalendar.class.getSimpleName(), AppCalendar.class, StoreKind.CONFIG);
        now = LocalDateTime.now();
        activities = outputData.getOutputActivities().stream()
                .map(OutputActivity::getName)
                .collect(Collectors.toList());
        unchangedAppEvents = new ArrayList<>();
        eventsToUpdate = new HashMap<>();

        try {
            if (appCalendar != null) appEvents = calendarService.getEvents();

            for (OutputActivity outputActivity : outputData.getOutputActivities()) {
                String activityName = outputActivity.getName();

                List<HourInterval> allHours =
                        outputActivity.getSuitableDaysHours().get(ConditionsType.ALL);
                if (allHours == null) continue;

                List<HourInterval> preferred =
                        outputActivity.getSuitableDaysHours().get(ConditionsType.PREFERRED);

                filterAndAddNewEvents(activityName, ConditionsType.ALL, allHours);
                if (preferred != null)
                    filterAndAddNewEvents(activityName, ConditionsType.PREFERRED, preferred);
            }

            if (appEvents != null && !appEvents.isEmpty()) updateChangedEvents();

        } catch (CalendarException e) {
            throw new OutputException(e.getMessage());
        }
    }

    private void filterAndAddNewEvents(String activityName,
                                       ConditionsType conditionsType,
                                       List<HourInterval> intervals) throws CalendarException {
        Outer:
        for (HourInterval interval : intervals) {

            if (appCalendar == null || appEvents == null || appEvents.isEmpty()) {
                calendarService
                        .addEvent(activityName, conditionsType, interval, location);
                continue;
            }

            for (AppEvent appEvent : appEvents) {
                if (!isValidEvent(appEvent)
                    || !appEvent.getActivity().equals(activityName)
                    || appEvent.getConditionsType() != conditionsType) {
                    continue;
                }

                HourInterval eventInterval = appEvent.getHourInterval();

                if (matching(eventInterval, interval)) {
                    unchangedAppEvents.add(appEvent);
                    continue Outer;
                }

                if (overlapping(eventInterval, interval)) {
                    if (eventsToUpdate.get(appEvent) == null) {
                        eventsToUpdate.put(appEvent, interval);
                    } else {
                        eventsToUpdate.remove(appEvent);
                    }
                    continue Outer;
                }
            }
            calendarService.addEvent(activityName, conditionsType, interval, location);
        }
    }

    private void updateChangedEvents() throws CalendarException {
        for (AppEvent appEvent : appEvents) {
            if (eventsToUpdate.get(appEvent) != null) {
                calendarService.updateEvent(appEvent.getEventId(), eventsToUpdate.get(appEvent));
                continue;
            }
            if (!unchangedAppEvents.contains(appEvent))
                calendarService.deleteEvent(appEvent.getEventId());
        }
    }

    private boolean isValidEvent(AppEvent event) {
        return event.getLocation().equals(location)
               && activities.contains(event.getActivity())
               && event.getHourInterval().getEnd().isAfter(now);
    }

    private boolean matching(HourInterval eventInterval, HourInterval outputInterval) {
        return eventInterval.equals(outputInterval) || (
                eventInterval.getEnd().equals(outputInterval.getEnd())
                && !eventInterval.getStart().isAfter(now)
                && !outputInterval.getStart().isAfter(now.plusHours(1))
        );
    }

    private boolean overlapping(HourInterval eventInterval, HourInterval outputInterval) {
        LocalDateTime eventStart = eventInterval.getStart();
        LocalDateTime eventEnd = eventInterval.getEnd();
        LocalDateTime outputStart = outputInterval.getStart();
        LocalDateTime outputEnd = outputInterval.getEnd();

        return (eventEnd.isAfter(outputStart) && eventEnd.isBefore(outputEnd)) ||
               (eventStart.isAfter(outputStart) && eventStart.isBefore(outputEnd)) ||
               (outputEnd.isAfter(eventStart) && outputEnd.isBefore(eventEnd)) ||
               (outputStart.isAfter(eventStart) && outputStart.isBefore(eventEnd));
    }
}
