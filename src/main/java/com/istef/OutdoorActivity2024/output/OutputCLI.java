package com.istef.OutdoorActivity2024.output;

import com.istef.OutdoorActivity2024.model.HourInterval;
import com.istef.OutdoorActivity2024.model.io.OutputData;
import com.istef.OutdoorActivity2024.model.io.OutputActivity;
import com.istef.OutdoorActivity2024.model.io.PrintHours;
import com.istef.OutdoorActivity2024.model.io.ConditionsType;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DataOutputer implementation that directs the output to the CLI.
 *
 * @see DataOutputer
 */
public class OutputCLI implements DataOutputer {

    @Override
    public void output(@Nullable final OutputData outputData) {
        if (outputData == null) {
            System.out.println("No suitable hours forecast!");
            return;
        }

        for (OutputActivity outputActivity : outputData.getOutputActivities()) {
            System.out.println("==================");
            System.out.println(outputActivity.getName());
            System.out.println("==================");

            List<HourInterval> allHours = outputActivity.getSuitableDaysHours().get(ConditionsType.ALL);
            if (allHours == null) {
                System.out.println("No suitable hours forecast!\n");
                continue;
            }

            List<PrintHours> preferred
                    = toPrintHours(outputActivity.getSuitableDaysHours().get(ConditionsType.PREFERRED));
            System.out.println("Preferred hours:");
            if (preferred == null || preferred.isEmpty()) {
                System.out.println("No suitable hours forecast!\n");
            } else {
                System.out.println(Arrays.deepToString(preferred.toArray()) + '\n');
            }

            List<PrintHours> all = toPrintHours(allHours);
            System.out.println("All suitable hours:");
            System.out.println(Arrays.deepToString(all.toArray()) + '\n');
        }

    }

    private List<PrintHours> toPrintHours(List<HourInterval> hourIntervals) {
        if (hourIntervals == null) return null;

        Map<LocalDate, List<HourInterval>> days = new HashMap<>();
        for (HourInterval hourInterval : hourIntervals) {

            LocalDateTime start = hourInterval.getStart();
            LocalDateTime end = hourInterval.getEnd();

            LocalDate dateStart = start.toLocalDate();
            LocalDate dateEnd = end.toLocalDate();

            if (dateStart.isEqual(dateEnd)) {
                addInterval(hourInterval, days);
                continue;
            }

            int datesCount = (int) dateStart.until(dateEnd, ChronoUnit.DAYS) + 1;
            HourInterval tempHourInterval = hourInterval.clone();
            for (int i = 0; i < datesCount; i++) {
                LocalDate date = dateStart.plusDays(i);

                LocalDateTime tempStart = tempHourInterval.getStart();

                if (i == datesCount - 1) {
                    LocalDateTime tempEnd = tempHourInterval.getEnd();
                    HourInterval hourInterval_2 = new HourInterval(tempEnd.withHour(0), tempEnd);
                    addInterval(hourInterval_2, days);
                    continue;
                }
                addInterval(new HourInterval(tempStart, tempStart.withHour(23)), days);
                LocalDateTime nextDayFirst = tempStart.withHour(23).plusHours(1);
                LocalDateTime tempEnd = tempHourInterval.getEnd();
                tempHourInterval = new HourInterval(nextDayFirst, tempEnd);
            }

        }

        List<PrintHours> printHours = new ArrayList<>();
        List<LocalDate> datesSorted = days.keySet().stream().sorted().collect(Collectors.toList());
        for (
                LocalDate date : datesSorted) {
            printHours.add(new PrintHours(date, days.get(date)));
        }
        return printHours;
    }

    private void addInterval(HourInterval hourInterval, Map<LocalDate, List<HourInterval>> days) {
        LocalDate date = hourInterval.getStart().toLocalDate();
        List<HourInterval> dateHours = days.get(date);
        if (dateHours != null) dateHours.add(hourInterval);
        else {
            dateHours = new ArrayList<>();
            dateHours.add(hourInterval);
            days.put(date, dateHours);
        }
    }

}
