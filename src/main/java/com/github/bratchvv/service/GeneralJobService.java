package com.github.bratchvv.service;

import com.github.bratchvv.api.SchedulerJob;
import com.github.bratchvv.controller.dto.QuartzRequest;
import com.github.bratchvv.utils.CronExpressionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * HTTP Service for jobs executing.
 *
 * @author Vladimir Bratchikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralJobService {

    private final SchedulerFactoryBean schedulerBean;

    /**
     * Run job.
     *
     * @param request {@link QuartzRequest}
     * @return HTTP response for controller.
     */
    public ResponseEntity<?> executeJob(QuartzRequest request) {
        SchedulerJob job = request.getJob();
        try {
            schedulerBean.getScheduler().triggerJob(new JobKey(job.getKey(), job.getGroup()));
            return ResponseEntity.ok(String.format("Job %s was executed", job));
        } catch (SchedulerException e) {
            log.error("Cannot execute job {} because ", job, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Cannot execute job %s because %s ", job, e.getMessage()));
        }
    }

    /**
     * Update existing job.
     *
     * @param request {@link QuartzRequest}
     * @param newCronExpression cron expression
     * @return HTTP response for controller.
     */
    public ResponseEntity<?> updateCronExpressionForJob(QuartzRequest request, String newCronExpression) {
        CronExpressionUtils.validateExpression(newCronExpression);
        try {
            Scheduler scheduler = schedulerBean.getScheduler();
            TriggerKey triggerKey = new TriggerKey(request.getJob().getTriggerKey(), request.getJob().getGroup());
            CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
            trigger.setCronExpression(newCronExpression);
            scheduler.rescheduleJob(triggerKey, trigger);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("Cron expression %s was updated", newCronExpression));
        } catch (SchedulerException | ParseException e) {
            log.error("Can not update cron expression {} because ", newCronExpression, e);
            return ResponseEntity.ok(
                    String.format("Can not update cron expression %s because %s", newCronExpression, e.getMessage())
            );
        }
    }

    /**
     * Pause existing job.
     *
     * @param request {@link QuartzRequest}
     * @return HTTP response for controller.
     */
    public ResponseEntity<?> pauseRunningJob(QuartzRequest request) {
        SchedulerJob job = request.getJob();
        try {
            schedulerBean.getScheduler().pauseJob(new JobKey(job.getKey(), job.getGroup()));
            return ResponseEntity.ok(String.format("Job %s was paused", job));
        } catch (SchedulerException e) {
            log.error("Can not pause the job {}, because", job, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Cannot paused job %s because %s", job, e.getMessage()));
        }
    }

    /**
     * Resume paused existing job.
     *
     * @param request {@link QuartzRequest}
     * @return HTTP response for controller.
     */
    public ResponseEntity<?> resumePausedJob(QuartzRequest request) {
        SchedulerJob job = request.getJob();
        try {
            schedulerBean.getScheduler().resumeJob(new JobKey(job.getKey(), job.getGroup()));
            return ResponseEntity.ok(String.format("Job %s was resumed", job));
        } catch (SchedulerException e) {
            log.error("Job {} was not resumed because ", job, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Job %s wasn't resumed because %s", job, e.getMessage()));
        }
    }
}
