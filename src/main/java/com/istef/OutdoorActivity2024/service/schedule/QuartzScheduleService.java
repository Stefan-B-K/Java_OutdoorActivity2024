package com.istef.OutdoorActivity2024.service.schedule;


import com.istef.OutdoorActivity2024.exceptios.ScheduleException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * ScheduleService implementation via the
 * <a href="https://www.quartz-scheduler.org">Quartz Job Scheduler</a> API
 *
 * @see ScheduleService
 */
public class QuartzScheduleService implements ScheduleService {
    private static final int DELAY_SECONDS = 2;
    private final SchedulerFactory sf;

    /**
     * ScheduleService implementation via the
     * <a href="https://www.quartz-scheduler.org">Quartz Job Scheduler</a> API
     *
     * @see ScheduleService
     */
    public QuartzScheduleService() {
        sf = new StdSchedulerFactory();
    }


    @Override
    public void schedule(Runnable task) throws ScheduleException {

        try {
            Scheduler scheduler = sf.getScheduler();

            JobDetail jobDetail = JobBuilder.newJob(((Job) task).getClass())
                    .withIdentity("updateSuitableHours", "forecast")
                    .build();
            Trigger trigger = newTrigger()
                    .withIdentity("hourly", "forecast")
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(15)
                            .repeatForever())
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.startDelayed(DELAY_SECONDS);
        } catch (SchedulerException e) {
            throw new ScheduleException(e.getMessage());
        }

    }
}

