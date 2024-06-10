package com.istef.OutdoorActivity2024.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.istef.OutdoorActivity2024.OutdoorActivityApp;
import com.istef.OutdoorActivity2024.exceptios.OutputException;
import com.istef.OutdoorActivity2024.model.HourInterval;
import com.istef.OutdoorActivity2024.model.io.*;
import com.istef.OutdoorActivity2024.service.mail.MailService;
import com.istef.OutdoorActivity2024.service.mail.jakarta.MailServiceJakarta;
import com.istef.OutdoorActivity2024.service.persistence.FilePersistService;
import com.istef.OutdoorActivity2024.service.persistence.PersistenceService;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DataOutputer implementation that sends the output as an e-mail.
 *
 * @see DataOutputer
 */
public class OutputMail implements DataOutputer {
    private final static String TEMP_FILE_DIR = "temp";
    private final static String TEMP_FILE_EXT = ".json";

    private final String mailSubject;
    private String[] mailBody;
    private File jsonFile;

    private final PersistenceService persistenceService;
    private final MailService mailService;

    /**
     * DataOutputer implementation that sends the output as an e-mail.
     *
     * @see DataOutputer
     */
    public OutputMail() {
        this.persistenceService = FilePersistService.getInstance();
        this.mailService = new MailServiceJakarta();
        this.mailSubject = "Suitable hours for Outdoor Activities";
    }

    @Override
    public void output(@Nullable OutputData outputData) throws OutputException {
        composeMail(outputData);

        mailService.sendMail("stefan_b_k@icloud.com", mailSubject, mailBody, jsonFile);
        jsonFile.delete();
        jsonFile.getParentFile().delete();
    }


    private void composeMail(@Nullable OutputData outputData) throws OutputException {
        if (outputData == null) {
            mailBody = new String[]{"No suitable hours in the forecast!"};
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (OutputActivity outputActivity : outputData.getOutputActivities()) {
            sb.append("==================\n")
                    .append(outputActivity.getName())
                    .append("\n==================\n\n");

            List<HourInterval> allHours =
                    outputActivity.getSuitableDaysHours().get(ConditionsType.ALL);
            if (allHours == null) {
                sb.append("No suitable hours forecast!\n\n");
                continue;
            }

            List<PrintHours> preferred = toPrintHours(outputActivity
                    .getSuitableDaysHours().get(ConditionsType.PREFERRED));
            sb.append("Preferred hours:\n");
            if (preferred == null || preferred.isEmpty()) {
                sb.append("No suitable hours forecast!\n\n");
            } else {
                sb.append(Arrays.deepToString(preferred.toArray())).append("\n\n");
            }

            List<PrintHours> all = toPrintHours(allHours);
            sb.append("All suitable hours:\n");
            sb.append(Arrays.deepToString(all.toArray())).append("\n\n");
        }
        mailBody = new String[]{sb.toString()};

        jsonFile = tempJsonFile();
        System.out.println("New mail composed.");
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
                LocalDateTime tempStart = tempHourInterval.getStart();

                if (i == datesCount - 1) {
                    LocalDateTime tempEnd = tempHourInterval.getEnd();
                    HourInterval hourInterval_2 =
                            new HourInterval(tempEnd.withHour(0), tempEnd);
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
        List<LocalDate> datesSorted = days.keySet().stream()
                .sorted().collect(Collectors.toList());
        for (
                LocalDate date : datesSorted) {
            printHours.add(new PrintHours(date, days.get(date)));
        }
        return printHours;
    }

    private void addInterval(HourInterval hourInterval,
                             Map<LocalDate, List<HourInterval>> days) {
        LocalDate date = hourInterval.getStart().toLocalDate();
        List<HourInterval> dateHours = days.get(date);
        if (dateHours != null) dateHours.add(hourInterval);
        else {
            dateHours = new ArrayList<>();
            dateHours.add(hourInterval);
            days.put(date, dateHours);
        }
    }

    private File tempJsonFile() throws OutputException {
        String storeId = OutputData.class.getSimpleName();
        OutputData data = persistenceService
                .getData(storeId, OutputData.class, StoreKind.DATA);
        String fileName = PersistenceService.camelToSnake(storeId);
        File jsonFile = Path
                .of(OutdoorActivityApp.APP_DIR_PATH,
                        StoreKind.DATA.name().toLowerCase(),
                        TEMP_FILE_DIR,
                        fileName + TEMP_FILE_EXT)
                .toFile();

        if (!jsonFile.getParentFile().exists())
            jsonFile.getParentFile().mkdirs();

        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.writeValue(jsonFile, data);
        } catch (IOException e) {
            throw new OutputException(e.getMessage());
        }

        return jsonFile;
    }

}
