package com.github.bratchvv.service.job;

import com.github.bratchvv.api.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Failed Job business logic.
 *
 * @author Vladimir Bratchikov
 */
@Slf4j
@PersistJobDataAfterExecution
@Component
public class FailedJob extends QuartzJob {

    @Override
    public final void accept(JobExecutionContext context) {

        log.info("Starting fail job at {}", Instant.now());

        throw new RuntimeException("Ooops...");
    }

    @Override
    protected SchedulerJob getJobType() {
        return SchedulerJob.FAILED_JOB;
    }

}
