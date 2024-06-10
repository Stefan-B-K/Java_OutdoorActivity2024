package com.istef.OutdoorActivity2024.input;

import com.istef.OutdoorActivity2024.OutdoorActivityApp;
import com.istef.OutdoorActivity2024.conditions.Condition;
import com.istef.OutdoorActivity2024.conditions.Conditions;
import com.istef.OutdoorActivity2024.exceptios.InputException;
import com.istef.OutdoorActivity2024.model.Units;
import com.istef.OutdoorActivity2024.model.io.InputActivity;
import com.istef.OutdoorActivity2024.model.io.InputData;
import com.istef.OutdoorActivity2024.model.io.StoreKind;
import com.istef.OutdoorActivity2024.service.UIService;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * DataInputer implementation, reading all the input data from CSV file.
 * Creates and opens the file, if it is not filled in by the user.
 * Reads the data on the next program run, if the user has filled in the file.
 *
 * @see DataInputer
 */
public class InputCSV implements DataInputer {
    private final static String INPUT_FILE_PATH = "activities.csv";
    private File csv;

    @Override
    public InputData getInputs() throws InputException {

        csv = Path.of(OutdoorActivityApp.APP_DIR_PATH,
                        StoreKind.CONFIG.name().toLowerCase(),
                        INPUT_FILE_PATH)
                .toFile();

        if (!csv.exists()) {
            UIService.csvFileDialog();
            displayCsvFile();
            return null;
        }

        InputData inputData = readDataFromCsv();
        if (inputData == null) {
            UIService.csvFileDialog();
            displayCsvFile();
        }

        return inputData;
    }

    private InputData readDataFromCsv() throws InputException {

        try (CSVReader csvReader = new CSVReader(new FileReader(csv), ',', '"')) {
            String city = readHeaderRow(csvReader.readNext());
            String daysAhead = readHeaderRow(csvReader.readNext());
            String unitsString = readHeaderRow(csvReader.readNext());
            String minConsecutiveHours = readHeaderRow(csvReader.readNext());

            if (city.isEmpty() || daysAhead.isEmpty() || unitsString.isEmpty()
                || minConsecutiveHours.isEmpty()) return null;

            Units units = Units.valueOf(unitsString.toUpperCase());

            List<InputActivity> inputActivities = null;
            boolean addToList = false;
            Iterator<Conditions> iterator = Arrays.stream(Conditions.values()).iterator();
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                if (addToList) {
                    Conditions conditionType = iterator.next();
                    inputActivities =
                            addDataToActivities(row, inputActivities, conditionType, units);
                    continue;
                }

                if (row[0].trim().startsWith("Activity")) {
                    addToList = true;
                    inputActivities = addDataToActivities(row, inputActivities, null, null);
                }
            }

            return new InputData(
                    city,
                    Integer.parseInt(daysAhead),
                    units,
                    Integer.parseInt(minConsecutiveHours),
                    inputActivities);

        } catch (IOException e) {
            throw new InputException(e.getMessage());
        }
    }

    private String readHeaderRow(String[] row) {
        if (row.length < 2) return "";
        return row[1].trim();
    }

    private List<InputActivity> addDataToActivities(String[] row,
                                                    List<InputActivity> inputActivities,
                                                    Conditions conditionType,
                                                    Units units) throws InputException {
        if (inputActivities == null) {
            inputActivities = new ArrayList<>();
            for (int i = 1; i < row.length; i++) {
                if (row[i].trim().isEmpty()) break;
                inputActivities.add(new InputActivity(row[i].trim()));
            }
            return inputActivities;
        }

        int activitiesCount = inputActivities.size();

        for (int i = 1; i <= activitiesCount; i++) {
            String value = row[i].trim();
            if (value.isEmpty()) continue;

            InputActivity activity = inputActivities.get(i - 1);
            List<Condition> collection = activity.getConditions();

            if (value.endsWith("*")) {
                value = value.substring(0, value.length() - 1);
                collection = activity.getPreferences();
            }
            Condition condition = conditionType.init(value, units);
            collection.add(condition);
        }

        return inputActivities;
    }

    private  void displayCsvFile() throws InputException {
        if (!csv.getParentFile().exists()) csv.getParentFile().mkdirs();

        try {
            if (csv.exists()) {
                java.awt.Desktop.getDesktop().edit(csv);
                return;
            } else {
                if (!csv.createNewFile())
                    throw new InputException("Failed to create file " + csv.getName());
            }
        } catch (IOException e) {
            throw new InputException(e.getMessage());
        }

        try (FileWriter fw = new FileWriter(csv, true)) {

            fw.write("City\n");
            fw.write("Days-ahead forecast,2\n");
            fw.write("\"Units: (Metric, Imperial)\",Metric\n");
            fw.write("Minimum consecutive days,2\n\n");
            fw.write("* - preferable (not mandatory)\n");
            fw.write(",1,2,3\n");
            fw.write("Activity / Sport\n");

            for (Conditions condition : Conditions.values()) {
                fw.write('"' + condition.getLabel() + "\"\n");
            }
            java.awt.Desktop.getDesktop().edit(csv);
        } catch (IOException e) {
            throw new InputException(e.getMessage());
        }
    }
}
