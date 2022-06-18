package com.github.bratchvv.config;

import com.github.bratchvv.api.SchedulerJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;

/**
 * Scheduled configuration.
 *
 * @author Vladimir Bratchikov
 * @see QuartzJobBean
 */
@Configuration
public class ScheduledConfig {

    private static final Map<SchedulerJob, String> SCHEDULE_CRON_EXPRESSIONS = new EnumMap<>(SchedulerJob.class);

    @Value("${quartz.daily.cronExpression}")
    private String dailyCronExpression;

    @Value("${quartz.everyMinute.cronExpression}")
    private String everyMinuteCronExpression;

    @PostConstruct
    public void initMap() {
        SCHEDULE_CRON_EXPRESSIONS.put(SchedulerJob.DAILY_JOB, this.dailyCronExpression);
        SCHEDULE_CRON_EXPRESSIONS.put(SchedulerJob.FAILED_JOB, this.everyMinuteCronExpression);
        SCHEDULE_CRON_EXPRESSIONS.put(SchedulerJob.TEST_JOB, this.everyMinuteCronExpression);
    }

    @Bean
    public Map<SchedulerJob, String> scheduleCronExpressions() {
        return SCHEDULE_CRON_EXPRESSIONS;
    }
}
