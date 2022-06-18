package com.github.bratchvv.service.job;

import com.github.bratchvv.api.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Daily Job business logic.
 *
 * @author Vladimir Bratchikov
 */
@Slf4j
@PersistJobDataAfterExecution
@Service
public class DailyJob extends QuartzJob {

    public final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void accept(JobExecutionContext context) {
        log.info("Starting daily job at {}", Instant.now());
        atomicInteger.incrementAndGet();
    }

    @Override
    protected SchedulerJob getJobType() {
        return SchedulerJob.DAILY_JOB;
    }

}
