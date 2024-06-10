package com.istef.OutdoorActivity2024.service;

import com.istef.OutdoorActivity2024.exceptios.ForecastException;
import com.istef.OutdoorActivity2024.model.*;
import com.istef.OutdoorActivity2024.model.Forecast;
import com.istef.OutdoorActivity2024.model.ForecastHour;
import com.istef.OutdoorActivity2024.conditions.Condition;
import com.istef.OutdoorActivity2024.model.io.*;
import com.istef.OutdoorActivity2024.service.forecast.ForecastService;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service providing the suitable days/hours
 * for use in the OutdoorActivity2024 project
 */
public class SuitableWeatherService {
    private final ForecastService weatherForecast;
    private int minConsecutiveHours;

    public SuitableWeatherService(ForecastService weatherForecast) {
        this.weatherForecast = weatherForecast;
    }

    /**
     * Apply condition/filter to the list of hours with forecast data.
     *
     * @param inputData the container object with input params
     * @return for each input activity: the dates with suitable hour intervals grouped in two
     * result types - PREFERRED (as per preferred conditions) and
     * ALL (as per the general conditions)
     * @see InputData
     * @see OutputData
     * @see ConditionsType
     * @see HourInterval
     */
    public OutputData getSuitableHourIntervals(@NotNull InputData inputData) throws ForecastException {

        minConsecutiveHours = inputData.getMinConsecutiveHours();

        Forecast forecast =
                weatherForecast.getForecast(inputData.getCity(), inputData.getDaysAhead());

        List<ForecastHour> forecastHours = forecast.getForecastHours();

        List<OutputActivity> outputActivities = new ArrayList<>();
        for (InputActivity activity : inputData.getInputActivities()) {
            outputActivities.add(getHoursForActivity(activity, forecastHours));
        }

        return new OutputData(inputData.getCity(), inputData.getMinConsecutiveHours(), outputActivities);
    }

    private OutputActivity getHoursForActivity(InputActivity inputActivity,
                                               List<ForecastHour> forecastHours) {

        Map<ConditionsType, List<HourInterval>> resultHours = new HashMap<>();
        OutputActivity outputActivity = new OutputActivity(inputActivity.getName(), resultHours);

        for (Condition condition : inputActivity.getConditions()) {
            forecastHours = condition.filter(forecastHours);
        }
        List<HourInterval> suitableHours = hoursToIntervals(forecastHours);
        if (suitableHours.isEmpty()) return outputActivity;

        resultHours.put(ConditionsType.ALL, suitableHours);

        if (inputActivity.getPreferences().isEmpty()) return outputActivity;

        Set<ForecastHour> forecastHourSet = new HashSet<>();
        for (Condition preference : inputActivity.getPreferences()) {
            forecastHourSet.addAll(preference.filter(forecastHours));
        }
        if (forecastHourSet.isEmpty()) return outputActivity;
        forecastHours = new ArrayList<>(forecastHourSet).stream()
                .sorted(Comparator.comparing(ForecastHour::getLocalDateTime))
                .collect(Collectors.toList());

        List<HourInterval> preferredHours = hoursToIntervals(forecastHours);
        if (preferredHours.isEmpty()) return outputActivity;

        resultHours.put(ConditionsType.PREFERRED, preferredHours);

        List<HourInterval> nonOverlappingSuitableHours = new ArrayList<>();
        for (HourInterval hourInterval : suitableHours) {
            if (!preferredHours.contains(hourInterval))
                nonOverlappingSuitableHours.add(hourInterval);
        }
        if (nonOverlappingSuitableHours.isEmpty()) {
            resultHours.remove(ConditionsType.ALL);
        } else {
            resultHours.put(ConditionsType.ALL, nonOverlappingSuitableHours);
        }

        return outputActivity;
    }

    @NotNull
    private List<HourInterval> hoursToIntervals(List<ForecastHour> forecastHours) {
        List<HourInterval> hourIntervals = new ArrayList<>();
        if (forecastHours.isEmpty()) return hourIntervals;

        List<LocalDateTime> hours = forecastHours.stream()
                .map(ForecastHour::getLocalDateTime)
                .collect(Collectors.toList());

        if (minConsecutiveHours < 2) {
            return hours.stream()
                    .map(localTime -> new HourInterval(localTime, localTime))
                    .collect(Collectors.toList());
        }

        int start = 0, end = -1;
        for (int i = 1; i < hours.size(); i++) {
            LocalDateTime startHour = hours.get(start);
            if (hours.get(i).minusHours(1).isAfter(hours.get(i - 1))) {
                if (end > 0 && hours.get(end).plusHours(2)
                        .isAfter(startHour.plusHours(minConsecutiveHours))) {
                    hourIntervals.add(new HourInterval(startHour, hours.get(end)));
                }
                start = i;
                end = -1;
                continue;
            }
            end = i;
            if (i == hours.size() - 1 && hours.get(end).plusHours(2)
                    .isAfter(startHour.plusHours(minConsecutiveHours))) {
                hourIntervals.add(new HourInterval(startHour, hours.get(end)));
            }
        }

        return hourIntervals;
    }

}
