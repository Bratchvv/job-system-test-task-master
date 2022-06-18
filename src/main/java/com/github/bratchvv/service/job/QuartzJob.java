package com.github.bratchvv.service.job;

import com.github.bratchvv.api.SchedulerJob;
import com.github.bratchvv.entity.JobInstance;
import com.github.bratchvv.entity.Status;
import com.github.bratchvv.repository.JobInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Slf4j
@DisallowConcurrentExecution
public abstract class QuartzJob extends QuartzJobBean implements Consumer<JobExecutionContext> {

    @Autowired
    private JobInstanceRepository jobInstanceRepository;

    @Override
    @SuppressWarnings("All")
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobInstance jobInstance = new JobInstance();
        jobInstance.setJobKey(getJobType().name());
        jobInstance.setStatus(Status.NEW);
        jobInstance.setStartTime(LocalDateTime.now());
        jobInstance = jobInstanceRepository.save(jobInstance);
        try {
            log.info("Job {} is running", getJobType());
            accept(jobExecutionContext);
            jobInstance.setStatus(Status.DONE);
            jobInstance.setEndTime(LocalDateTime.now());
            jobInstance = jobInstanceRepository.save(jobInstance);
            log.info("Job {} successfully finished", getJobType());
        } catch (Exception e) {
            log.error("Job " + getJobType() + " failed!", e);
            handleError(jobExecutionContext, e);
            jobInstance.setStatus(Status.FAILED);
            jobInstance.setEndTime(LocalDateTime.now());
            jobInstance = jobInstanceRepository.save(jobInstance);
        } finally {
            finalizeJob(jobExecutionContext);
        }
    }

    protected abstract SchedulerJob getJobType();

    protected void handleError(JobExecutionContext context, Exception ex) {
        // custom exception handling logic
    }

    protected void finalizeJob(JobExecutionContext context) {
        // custom finalize logic
    }

}
