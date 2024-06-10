package com.istef.OutdoorActivity2024.service.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * ScheduleService implementation via the
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html">Java Class Executors</a>
 *
 * @see ScheduleService
 */
public class ExecutorScheduleService implements ScheduleService {
    private static final int DELAY_SECONDS = 2;
    private static final int UPDATE_INTERVAL_SECONDS = 15;

    private final ScheduledExecutorService scheduler;

    /**
     * ScheduleService implementation via the
     * <a href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html">Java Class Executors</a>
     *
     * @see ScheduleService
     */
    public ExecutorScheduleService() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void schedule(Runnable task) {
        scheduler.scheduleAtFixedRate(task,
                DELAY_SECONDS,
                UPDATE_INTERVAL_SECONDS,
                TimeUnit.SECONDS);
    }
}
