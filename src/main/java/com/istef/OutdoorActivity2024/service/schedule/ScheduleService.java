package com.istef.OutdoorActivity2024.service.schedule;

import com.istef.OutdoorActivity2024.exceptios.ScheduleException;

/**
 * Definition of scheduler service that periodically executes
 * the main application functionalities
 */
public interface ScheduleService {

    /**
     * @param task Runnable task to be periodically performed by the scheduler implementation
     * @see Runnable
     */
    void schedule(Runnable task) throws ScheduleException;
}
