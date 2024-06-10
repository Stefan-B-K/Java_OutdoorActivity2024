package com.istef.OutdoorActivity2024;

import com.istef.OutdoorActivity2024.exceptios.OutputException;
import com.istef.OutdoorActivity2024.model.HourInterval;
import com.istef.OutdoorActivity2024.model.io.OutputActivity;
import com.istef.OutdoorActivity2024.model.io.OutputData;
import com.istef.OutdoorActivity2024.model.io.ConditionsType;
import com.istef.OutdoorActivity2024.model.io.StoreKind;
import com.istef.OutdoorActivity2024.service.UpdaterService;
import com.istef.OutdoorActivity2024.service.persistence.FilePersistService;
import com.istef.OutdoorActivity2024.service.persistence.PersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdaterServiceTest {

    private final static String CITY = "Sofia";
    private final static String ACTIVITY = "Federball";
    private LocalDateTime pastHour;

    private final PersistenceService persistenceService = mock(FilePersistService.class);


    @BeforeEach
    void init() {
        LocalDateTime now = LocalDateTime.now();
        pastHour = LocalDateTime.now()
                .withHour(now.getHour()).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    public void changesInSavedDataFirstRun() throws OutputException {
        try (MockedStatic<FilePersistService> persistenceServiceMock = mockStatic(FilePersistService.class)) {
            persistenceServiceMock.when(FilePersistService::getInstance).thenReturn(persistenceService);
            when(persistenceService
                    .getData(OutputData.class.getSimpleName(), OutputData.class, StoreKind.DATA))
                    .thenReturn(null);
            OutputData dataNow = getOutput(pastHour.plusHours(1), pastHour.plusHours(9));
            UpdaterService updaterService = new UpdaterService();
            assertTrue(updaterService.changesInSavedData(dataNow));
        }
    }

    @Test
    public void changesInSavedDataNextHour() throws OutputException {
        try (MockedStatic<FilePersistService> persistenceServiceMock = mockStatic(FilePersistService.class)) {
            persistenceServiceMock.when(FilePersistService::getInstance).thenReturn(persistenceService);
            when(persistenceService
                    .getData(OutputData.class.getSimpleName(), OutputData.class, StoreKind.DATA))
                    .thenReturn(getOutput(pastHour, pastHour.plusHours(9)));
            OutputData dataNow = getOutput(pastHour.plusHours(1), pastHour.plusHours(9));
            UpdaterService updaterService = new UpdaterService();
            assertFalse(updaterService.changesInSavedData(dataNow));
        }
    }

    @Test
    public void changesInSavedDataNextHourShorterInterval() throws OutputException {
        try (MockedStatic<FilePersistService> persistenceServiceMock = mockStatic(FilePersistService.class)) {
            persistenceServiceMock.when(FilePersistService::getInstance).thenReturn(persistenceService);
            when(persistenceService
                    .getData(OutputData.class.getSimpleName(), OutputData.class, StoreKind.DATA))
                    .thenReturn(getOutput(pastHour, pastHour.plusHours(9)));
            OutputData dataNowShorter = getOutput(pastHour.plusHours(1), pastHour.plusHours(7));
            UpdaterService updaterService = new UpdaterService();
            assertTrue(updaterService.changesInSavedData(dataNowShorter));
        }
    }


    //  past hour intervals:    ALL = 9 hours    PREF = 4 hours
    private OutputData getOutput(LocalDateTime start, LocalDateTime end) {
        int minConsecutiveHours = 2;
        Map<ConditionsType, List<HourInterval>> suitableDaysHours = new HashMap<>();
        OutputActivity outputActivity = new OutputActivity(ACTIVITY, suitableDaysHours);
        OutputData outputData = new OutputData(CITY, minConsecutiveHours, List.of(outputActivity));

        int diff = (int) Duration.between(start, end).getSeconds() / 3600;
        if (diff < minConsecutiveHours) return outputData;

        HourInterval hourAll = new HourInterval(start, end);
        suitableDaysHours.put(ConditionsType.ALL, List.of(hourAll));

        diff = (int) Duration.between(start, end.minusHours(5)).getSeconds() / 3600;
        if (diff < minConsecutiveHours) return outputData;
        HourInterval hourPref = new HourInterval(start, end.minusHours(5));
        suitableDaysHours.put(ConditionsType.PREFERRED, List.of(hourPref));

        return outputData;
    }
}
