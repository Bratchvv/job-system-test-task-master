package com.github.bratchvv.service;

import com.github.bratchvv.api.SchedulerJob;
import com.github.bratchvv.controller.dto.QuartzRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * HTTP Scheduler service.
 *
 * @author Vladimir Bratchikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final SchedulerFactoryBean schedulerFactoryBean;
    private final Map<SchedulerJob, String> scheduleCronExpressions;


    /**
     * @param request {@link QuartzRequest}
     * @return true if job already exist.
     */
    public Boolean checkIfJobExists(QuartzRequest request) {
        SchedulerJob job = request.getJob();
        try {
            return schedulerFactoryBean.getScheduler().checkExists(new TriggerKey(job.getTriggerKey(), job.getGroup()));
        } catch (SchedulerException e) {
            log.error("checkIfJobExists method failed with", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Schedule specific job.
     *
     * @param request {@link QuartzRequest}
     * @return HTTP response for controller.
     */
    public ResponseEntity<?> scheduleJob(QuartzRequest request) {
        return scheduleJobs(filterJobsByRequest(request));
    }

    /**
     * Schedule all available jobs.
     *
     * @return HTTP response for controller.
     */
    public ResponseEntity<?> scheduleAllJobs() {
        return scheduleJobs(alwaysTrue());
    }

    /**
     * Unschedule selected jobs.
     *
     * @param request {@link QuartzRequest}
     * @return HTTP response for controller.
     */
    public ResponseEntity<?> unscheduleJobs(List<QuartzRequest> request) {
        try {
            List<TriggerKey> jobsForUnschedule = request.stream().map(QuartzRequest::getJob)
                    .filter(Objects::nonNull)
                    .map(job -> new TriggerKey(job.getTriggerKey(), job.getGroup())).collect(Collectors.toList());

            schedulerFactoryBean.getScheduler().unscheduleJobs(jobsForUnschedule);
            return ResponseEntity.ok("Jobs from request was unscheduled");
        } catch (Exception e) {
            log.error("Can not unschedule jobs because ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Can not unschedule jobs");
        }

    }

    /**
     * Schedule selected jobs.
     *
     * @param filterJobs selected jobs
     * @return HTTP response for controller.
     */
    public ResponseEntity<?> scheduleJobs(Predicate<SchedulerJob> filterJobs) {
        AtomicInteger successfullSchedulerJobs = new AtomicInteger(0);
        AtomicInteger deniedToScheduleJobs = new AtomicInteger(0);
        AtomicInteger alreadyScheduledJobs = new AtomicInteger(0);

        Arrays.stream(SchedulerJob.values()).filter(filterJobs).forEach(job -> {
            try {
                schedulerFactoryBean.getScheduler().addJob(getJobDetails(job), true, true);
                String cronExpression = scheduleCronExpressions.get(job);
                if (!Objects.isNull(cronExpression) && !schedulerFactoryBean.getScheduler()
                        .checkExists(new TriggerKey(job.getTriggerKey(), job.getGroup()))) {
                    schedulerFactoryBean.getScheduler().scheduleJob(
                            getJobTrigger(CronScheduleBuilder.cronSchedule(cronExpression), job)
                    );
                    successfullSchedulerJobs.incrementAndGet();
                } else {
                    alreadyScheduledJobs.incrementAndGet();
                }
            } catch (Exception e) {
                log.error("Problem with scheduler jobs {} because ", job, e);
                deniedToScheduleJobs.incrementAndGet();
            }
        });

        return ResponseEntity.status(HttpStatus.OK)
                .body(String.format("Total jobs scheduled %s, already scheduled %s, failed %s",
                        successfullSchedulerJobs.get(), alreadyScheduledJobs.get(), deniedToScheduleJobs.get()));
    }

    /**
     * Get job details data.
     *
     * @param job job to request details {@link SchedulerJob}
     * @return job details.
     */
    public JobDetail getJobDetails(SchedulerJob job) {
        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setKey(new JobKey(job.getKey(), job.getGroup()));
        jobDetail.setJobClass(job.getJobClass());
        jobDetail.setDurability(job.getDurable());
        return jobDetail;
    }

    /**
     * @param schedulerBuilder buildetr
     * @param job              job to request details {@link SchedulerJob}
     * @param <T>              trigger type.
     * @return job trigger data
     */
    public <T extends Trigger> Trigger getJobTrigger(ScheduleBuilder<T> schedulerBuilder, SchedulerJob job) {
        return newTrigger().forJob(getJobDetails(job))
                .withIdentity(job.getTriggerKey(), job.getGroup())
                .withSchedule(schedulerBuilder)
                .build();
    }

    private Predicate<SchedulerJob> alwaysTrue() {
        return job -> true;
    }

    private Predicate<SchedulerJob> filterJobsByRequest(QuartzRequest request) {
        return job -> request != null && request.getJob().equals(job);
    }

}
