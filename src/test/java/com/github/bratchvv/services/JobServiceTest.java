package com.github.bratchvv.services;

import com.github.bratchvv.BaseSpringBootTest;
import com.github.bratchvv.api.SchedulerJob;
import com.github.bratchvv.entity.JobInstance;
import com.github.bratchvv.repository.JobInstanceRepository;
import com.github.bratchvv.controller.dto.QuartzRequest;
import com.github.bratchvv.service.GeneralJobService;
import com.github.bratchvv.service.SchedulerService;
import lombok.SneakyThrows;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.time.LocalDateTime;
import java.util.Date;

import static java.lang.Thread.sleep;


class JobServiceTest extends BaseSpringBootTest {

    @Autowired
    private GeneralJobService generalJobService;

    @Autowired
    private SchedulerService schedulerService;

    @SpyBean
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @MockBean
    @Autowired
    private JobInstanceRepository jobInstanceRepository;

    private static String toCron(LocalDateTime time) {
        return String.format("%s %s %s ? * * *", time.getSecond(), time.getMinute(), time.getHour());
    }

    @AfterEach
    void cleanUp() {
        this.jobInstanceRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    void runningJob() {
        JobInstance jobInstance = new JobInstance();
        jobInstance.setId(new ObjectId());
        Mockito.when(jobInstanceRepository.save(Mockito.any())).thenReturn(jobInstance);
        QuartzRequest quartzRequest = new QuartzRequest(SchedulerJob.DAILY_JOB);
        schedulerService.scheduleJob(quartzRequest);
        String testCron = toCron(LocalDateTime.now().plusSeconds(10));

        generalJobService.updateCronExpressionForJob(quartzRequest, testCron);

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey("dailyJobTrigger", "dailyJobGroup"));
        Assertions.assertNull(trigger.getPreviousFireTime());
        Date nextFireTime = trigger.getNextFireTime();

        sleep(15_000);
        trigger = scheduler.getTrigger(TriggerKey.triggerKey("dailyJobTrigger", "dailyJobGroup"));
        Date previousFireTime = trigger.getPreviousFireTime();
        Assertions.assertNotNull(previousFireTime);

        Assertions.assertEquals(previousFireTime, nextFireTime);
    }
}
