package com.istef.OutdoorActivity2024;

import com.istef.OutdoorActivity2024.exceptios.ForecastException;
import com.istef.OutdoorActivity2024.exceptios.InputException;
import com.istef.OutdoorActivity2024.model.*;
import com.istef.OutdoorActivity2024.model.io.*;
import com.istef.OutdoorActivity2024.service.SuitableWeatherService;
import com.istef.OutdoorActivity2024.service.forecast.ForecastServiceWeatherApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.istef.OutdoorActivity2024.conditions.Conditions.*;
import static com.istef.OutdoorActivity2024.conditions.Conditions.CLOUDS_MIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SuitableWeatherServiceTest {

    private final static String CITY = "Sofia";
    private final static String ACTIVITY = "Federball";
    private final static ZoneId ZONE_ID = ZoneId.systemDefault();

    @Mock
    private ForecastServiceWeatherApi weatherApiService;

    @InjectMocks
    private SuitableWeatherService suitableWeatherService;

    @Test
    public void getSuitableHourIntervals() throws ForecastException, InputException {

        when(weatherApiService.getForecast("Sofia", 2))
                .thenReturn(getForecast());

        OutputData outputData = suitableWeatherService.getSuitableHourIntervals(getInput());
        List<OutputActivity> outputActivities = outputData.getOutputActivities();
        assertEquals(1, outputActivities.size());

        OutputActivity federball = outputActivities.get(0);

        assertEquals(ACTIVITY, federball.getName());

        Map<ConditionsType, List<HourInterval>> suitableDaysHours = federball.getSuitableDaysHours();
        assertEquals(2, suitableDaysHours.size());

        List<HourInterval> preferredHours = suitableDaysHours.get(ConditionsType.PREFERRED);
        assertEquals(2, preferredHours.size());

        HourInterval day2 = preferredHours.get(0);
        assertEquals(15, day2.getStart().getHour());
        assertEquals(19, day2.getEnd().getHour());

        HourInterval day3 = preferredHours.get(1);
        assertEquals(12, day3.getStart().getHour());
        assertEquals(18, day3.getEnd().getHour());

        List<HourInterval> allHours = suitableDaysHours.get(ConditionsType.ALL);
        assertEquals(2, allHours.size());

        day2 = allHours.get(0);
        assertEquals(8, day2.getStart().getHour());
        assertEquals(20, day2.getEnd().getHour());

        day3 = allHours.get(1);
        assertEquals(12, day3.getStart().getHour());
        assertEquals(18, day3.getEnd().getHour());
    }

    //  DAY, minTemp 5 oC       Weekend, Clouds > 50%
    private InputData getInput() throws InputException {
        int daysAheadForecast = 2;
        Units units = Units.METRIC;
        int minConsecutiveHours = 2;

        return new InputData(CITY,
                daysAheadForecast,
                units,
                minConsecutiveHours,
                List.of(new InputActivity(
                        ACTIVITY,
                        List.of(DAYTIME.init(DayTime.DAY.name(), units),
                                TEMPERATURE_MIN.init("5", units)),
                        List.of(WEEKDAY.init(WeekDay.WEEKEND.name(), units),
                                CLOUDS_MIN.init("50", units))))
        );
    }

    // DAY: (8-20)h
    // THURSDAY(TODAY):     temp 0      - all day       clouds 0%   - all day
    // FRIDAY:              temp 10 oC  - all day       clouds 70%  - (15-19)h
    // SATURDAY:            temp 15 oC  - (12-18)h      clouds 0%   - all day
    private Forecast getForecast() {
        Forecast forecast = new Forecast();
        forecast.setCity(CITY);
        forecast.setTimezoneId(ZONE_ID.toString());
        int sunrise = 8, sunset = 20;
        int startTimeTemp = 12, endTimeTemp = 18;
        int startTimeClouds = 15, endTimeClouds = 19;

        // Thursday Friday Saturday
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime hour = now
                .withYear(2024).withMonth(5).withDayOfMonth(2)
                .withHour(now.getHour() + 1).withMinute(0).withSecond(0).withNano(0);

        List<ForecastHour> forecastHours = new ArrayList<>();
        for (int day = 1; day <= 3; day++) {
            int startHour = day == 1 ? now.getHour() + 1 : 0;
            for (int i = startHour; i < 24; i++) {
                float temp = day == 2 ? 10F : 0F;
                int clouds = 0;
                DayTime dayTime = (i < sunrise || i > sunset) ? DayTime.NIGHT : DayTime.DAY;
                if (day == 2 && i >= startTimeClouds && i <= endTimeClouds)
                    clouds = 70;
                if (day == 3 && i >= startTimeTemp && i <= endTimeTemp)
                    temp = 15F;
                forecastHours.add(getForecastHour(hour, dayTime, temp, clouds));
                hour = hour.plusHours(1);
            }
        }

        forecast.setForecastHours(forecastHours);

        return forecast;
    }

    private ForecastHour getForecastHour(LocalDateTime localDateTime, DayTime dayTime, Float tempC, int clouds) {

        return new ForecastHour(
                localDateTime,
                dayTime,
                WeekDay.from(localDateTime.toInstant(ZoneOffset.UTC), ZONE_ID),
                new UnitValue<>(tempC, 0F),
                new UnitValue<>(20F, 0F),
                new UnitValue<>(30F, 0F),
                new UnitValue<>(1000F, 0F),
                new UnitValue<>(1000, 0),
                0, clouds, 0, 0, 0
        );
    }
}
