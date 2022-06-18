package com.github.bratchvv.api;

import com.github.bratchvv.service.job.DailyJob;
import com.github.bratchvv.service.job.FailedJob;
import com.github.bratchvv.service.job.TestJob;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Job types enum.
 *
 * @see QuartzJobBean
 * @author Vladimir Bratchikov
 */
@RequiredArgsConstructor
public enum SchedulerJob {

    DAILY_JOB("dailyJob", DailyJob.class, true),
    FAILED_JOB("failedJob", FailedJob.class, true),
    TEST_JOB("testJob", TestJob.class, true);

    private final String jobName;
    @Getter
    private final Class<? extends QuartzJobBean> jobClass;
    @Getter
    private final Boolean durable;

    public String getTriggerKey() {
        return jobName + "Trigger";
    }

    public String getGroup() {
        return jobName + "Group";
    }

    public String getKey() {
        return jobName + "Key";
    }

}
