package com.istef.OutdoorActivity2024.service;

import com.istef.OutdoorActivity2024.exceptios.OutputException;
import com.istef.OutdoorActivity2024.model.HourInterval;
import com.istef.OutdoorActivity2024.model.io.OutputActivity;
import com.istef.OutdoorActivity2024.model.io.OutputData;
import com.istef.OutdoorActivity2024.model.io.ConditionsType;
import com.istef.OutdoorActivity2024.model.io.StoreKind;
import com.istef.OutdoorActivity2024.service.persistence.FilePersistService;
import com.istef.OutdoorActivity2024.service.persistence.PersistenceService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that checks for changes in the OutputData
 *
 * @see OutputData
 */
public class UpdaterService {
    private final PersistenceService persistenceService;

    /**
     * Service that checks for changes in the OutputData
     *
     * @see OutputData
     */
    public UpdaterService( ) {
        this.persistenceService = FilePersistService.getInstance();
    }

    /**
     * Service that checks for changes in the OutputData
     *
     * @param newData the latest/updated OutputData
     * @return true/false if there is/isn't change in the OutputData,
     * previously provided to the user
     * @see OutputData
     */
    public boolean changesInSavedData(final OutputData newData) throws OutputException {

        OutputData previousData = persistenceService
                .getData(OutputData.class.getSimpleName(), OutputData.class, StoreKind.DATA);

        if (previousData == null)
            return update(newData, " NEW INPUT");

        OutputData previousDataTrimmed = filterFutureData(previousData);

        if (newData.compareTo(previousDataTrimmed) != 0)
            return update(newData, " CHANGE");

        System.out.println(LocalTime.now() + " NO CHANGE");
        return false;
    }

    private boolean update(final OutputData newData, String message) throws OutputException {
        System.out.println(LocalTime.now() + message);
        persistenceService.saveData(newData, StoreKind.DATA);
        return true;
    }

    private OutputData filterFutureData(final OutputData outputData) {
        List<OutputActivity> outputActivities = outputData.getOutputActivities();
        List<OutputActivity> outputActivitiesFiltered = new ArrayList<>();
        for (OutputActivity outputActivity : outputActivities) {
            String name = outputActivity.getName();
            Map<ConditionsType, List<HourInterval>> suitableDaysHours = outputActivity.getSuitableDaysHours();
            Map<ConditionsType, List<HourInterval>> filteredDaysHours = new HashMap<>();
            OutputActivity activityFiltered = new OutputActivity(name, filteredDaysHours);

            if (suitableDaysHours.isEmpty()) {
                outputActivitiesFiltered.add(activityFiltered);
                continue;
            }

            for (ConditionsType conditionsType : suitableDaysHours.keySet()) {
                List<HourInterval> hourIntervals = suitableDaysHours.get(conditionsType);
                List<HourInterval> filterFutureHours
                        = filterFutureHours(hourIntervals, outputData.getMinConsecutiveHours());
                if (filterFutureHours.isEmpty()) continue;
                filteredDaysHours.put(conditionsType, filterFutureHours);
            }
            outputActivitiesFiltered.add(activityFiltered);
        }
        return new OutputData(outputData.getCity(),
                outputData.getMinConsecutiveHours(),
                outputActivitiesFiltered);
    }

    private List<HourInterval> filterFutureHours(List<HourInterval> hourIntervals, int minIntervalLength) {
        List<HourInterval> filteredHours = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minStart = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime minEnd = minStart.plusHours(minIntervalLength);

        for (HourInterval hourInterval : hourIntervals) {
            if (hourInterval.getEnd().isBefore(minEnd)) continue;

            HourInterval hourIntervalFiltered = new HourInterval(hourInterval.getStart(), hourInterval.getEnd());
            if (hourInterval.getStart().isBefore(now))
                hourIntervalFiltered.setStart(minStart);

            filteredHours.add(hourIntervalFiltered);
        }

        return filteredHours;
    }

}
