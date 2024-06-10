package org.example;

import com.istef.OutdoorActivity2024.OutdoorActivityApp;
import com.istef.OutdoorActivity2024.exceptios.ScheduleException;
import com.istef.OutdoorActivity2024.service.schedule.QuartzScheduleService;
import com.istef.OutdoorActivity2024.service.schedule.ScheduleService;

public class Main {

    public static void main(String[] args) {
        run(false);
    }

    static void run(boolean runContinuously) {
        OutdoorActivityApp outdoorActivity = new OutdoorActivityApp();
        if (!runContinuously) outdoorActivity.run();
        else {
            try {
                final ScheduleService scheduler = new QuartzScheduleService();
//            final ScheduleService scheduler = new ExecutorScheduleService();
                scheduler.schedule(outdoorActivity);
            } catch (ScheduleException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}




