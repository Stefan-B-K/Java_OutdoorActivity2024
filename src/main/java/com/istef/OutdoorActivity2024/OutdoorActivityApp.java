package com.istef.OutdoorActivity2024;

import com.istef.OutdoorActivity2024.exceptios.CalendarException;
import com.istef.OutdoorActivity2024.exceptios.ForecastException;
import com.istef.OutdoorActivity2024.exceptios.InputException;
import com.istef.OutdoorActivity2024.exceptios.OutputException;
import com.istef.OutdoorActivity2024.input.DataInputer;
import com.istef.OutdoorActivity2024.input.InputCSV;
import com.istef.OutdoorActivity2024.model.io.InputData;
import com.istef.OutdoorActivity2024.model.io.OutputData;
import com.istef.OutdoorActivity2024.model.io.OutputUserChoice;
import com.istef.OutdoorActivity2024.model.io.StoreKind;
import com.istef.OutdoorActivity2024.output.DataOutputer;
import com.istef.OutdoorActivity2024.output.OutputCLI;
import com.istef.OutdoorActivity2024.output.OutputGoogleCalendar;
import com.istef.OutdoorActivity2024.output.OutputMail;
import com.istef.OutdoorActivity2024.service.SuitableWeatherService;
import com.istef.OutdoorActivity2024.service.UIService;
import com.istef.OutdoorActivity2024.service.UpdaterService;
import com.istef.OutdoorActivity2024.service.calendar.google.CalendarServiceGoogle;
import com.istef.OutdoorActivity2024.service.forecast.ForecastService;
import com.istef.OutdoorActivity2024.service.forecast.ForecastServiceWeatherApi;
import com.istef.OutdoorActivity2024.service.persistence.FilePersistService;
import com.istef.OutdoorActivity2024.service.persistence.PersistenceService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalTime;

import static java.lang.System.exit;


/**
 * The central manager class of the OutdoorActivity2024 project
 */
public class OutdoorActivityApp implements Runnable, Job {

    public static final String PROJECT_NAME = "OutdoorActivity2024";
    public static final String APP_NAME = "Outdoor Activity 2024";
    public static String APP_DIR_PATH;

    private static final String USER_HOME = "user.home";

    private final DataInputer dataLoader;
    private final ForecastService weatherForecast;
    private final DataOutputer dataOutputer;
    private OutputUserChoice outputUserChoice;

    /**
     * The central task of the OutdoorActivity2024 project
     * that can be scheduled via Executors (as a Runnable)
     * or via Quartz Job Scheduler (as a Job)
     *
     * @see Runnable
     * @see Job
     */
    public OutdoorActivityApp() {
        appConfig();
        this.dataLoader =  new InputCSV(); //  new InputCLI();
        this.weatherForecast = new ForecastServiceWeatherApi(); // new ForecastServiceOpenWeatherMap();

        if (outputUserChoice == null) {
            this.dataOutputer = new OutputCLI();
            return;
        }

        switch (outputUserChoice.getSelectedOutput()) {
            case MAIL:
                this.dataOutputer = new OutputMail();
                break;
            case GOOGLE_CALENDAR:
                this.dataOutputer = new OutputGoogleCalendar();
                try {
                    CalendarServiceGoogle.initialUserAuth();
                } catch (CalendarException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                this.dataOutputer = new OutputCLI();
        }
    }

    @Override
    public void run() {

        final SuitableWeatherService suitableWeatherService =
                new SuitableWeatherService(weatherForecast);

        final UpdaterService updaterService = new UpdaterService();

        try {
            InputData inputs = dataLoader.getInputs();
            if (inputs == null) {
                System.err.println(LocalTime.now() + " Missing or invalid inputs!");
                exit(0);
            }

            OutputData outputData =
                    suitableWeatherService.getSuitableHourIntervals(inputs);

            if (updaterService.changesInSavedData(outputData))
                dataOutputer.output(outputData);

        } catch (InputException | ForecastException | OutputException e) {
            System.err.println(e.getMessage());
        }
    }

    private void appConfig() {
        String homeDir = System.getProperty(USER_HOME);
        File appDir = Path.of(homeDir, PROJECT_NAME).toFile();
        if (!appDir.exists()) appDir.mkdirs();
        APP_DIR_PATH = appDir.getPath();

        PersistenceService persistenceService = FilePersistService.getInstance();
        outputUserChoice =
                persistenceService.getData(OutputUserChoice.class.getSimpleName(),
                        OutputUserChoice.class,
                        StoreKind.CONFIG);
        if (outputUserChoice != null) return;

        UIService.outputChoiceDialog(outputChoice -> {
            try {
                persistenceService.saveData(outputChoice, StoreKind.CONFIG);
                outputUserChoice = outputChoice;
            } catch (OutputException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void execute(JobExecutionContext context) {
        run();
    }

}




