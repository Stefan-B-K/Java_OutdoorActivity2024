package com.istef.OutdoorActivity2024.input;

import com.istef.OutdoorActivity2024.conditions.*;
import com.istef.OutdoorActivity2024.exceptios.InputException;
import com.istef.OutdoorActivity2024.model.*;
import com.istef.OutdoorActivity2024.model.io.InputData;
import com.istef.OutdoorActivity2024.model.io.InputActivity;

import java.util.*;

import static com.istef.OutdoorActivity2024.conditions.Conditions.*;

/**
 * DataInputer implementation, reading city name from CLI,
 * with hardcoded configuration (units, conditions, preferences...)
 *
 * @see DataInputer
 */
public class InputCLI implements DataInputer {

    @Override
    public InputData getInputs() throws InputException {

        System.out.println("Please, enter a city: ");
        Scanner sc = new Scanner(System.in);
        String city = sc.nextLine().trim();
        sc.close();

        if (city.isEmpty()) return null;

        int daysAheadForecast = 2;
        Units units = Units.METRIC;
        int minConsecutiveHours = 2;

        List<Condition> conditions = List.of(
                DAYTIME.init(DayTime.DAY.name(), units),
                TEMPERATURE_MIN.init("5", units),
                TEMPERATURE_MAX.init("30", units),
                WIND_GUST_MAX.init("15", units),
                RAIN_CHANCE_MAX.init("50", units)
        );

        List<Condition> preferences = List.of(
                WEEKDAY.init(WeekDay.WEEKEND.name(), units),
                CLOUDS_MIN.init("50", units)
        );


        InputActivity inputActivity =
                new InputActivity("Federball", conditions, preferences);

        return new InputData(city,
                daysAheadForecast,
                units,
                minConsecutiveHours,
                List.of(inputActivity));
    }
}
