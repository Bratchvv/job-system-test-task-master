package com.github.bratchvv.service.job;

import com.github.bratchvv.api.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Test Job business logic.
 *
 * @author Vladimir Bratchikov
 */
@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Component
public class TestJob extends QuartzJob {

    @Override
    public void accept(JobExecutionContext basicJobContext) {

        log.info("Starting test job at {}", Instant.now());
    }

    @Override
    protected SchedulerJob getJobType() {
        return SchedulerJob.TEST_JOB;
    }

}
