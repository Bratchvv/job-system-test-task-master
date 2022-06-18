package com.github.bratchvv.controller;

import com.github.bratchvv.api.SchedulerJob;
import com.github.bratchvv.controller.dto.QuartzRequest;
import com.github.bratchvv.service.GeneralJobService;
import com.github.bratchvv.service.SchedulerService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SchedulerControllerTest extends BaseRestTest {

    private static final String BASE_URL = "/v1/scheduler";

    @Autowired
    private GeneralJobService generalJobService;

    @Autowired
    private SchedulerService schedulerService;

    @AfterEach
    void unscheduleAllJobs() {
        List<QuartzRequest> jobsToUnschedule = Arrays.stream(SchedulerJob.values()).map(QuartzRequest::new)
                .collect(Collectors.toList());
        schedulerService.unscheduleJobs(jobsToUnschedule);
    }

    @Test
    @SneakyThrows
    void scheduleAllJobs() {
        mvc.perform(post(BASE_URL + "/schedule-all-jobs"))
                .andExpect(status().isOk()).andReturn();
        Arrays.stream(SchedulerJob.values())
                .forEach(job -> assertTrue(schedulerService.checkIfJobExists(new QuartzRequest(job))));
    }

    @Test
    @SneakyThrows
    void scheduleJob() {
        QuartzRequest testJobRequest = new QuartzRequest(SchedulerJob.TEST_JOB);
        assertFalse(schedulerService.checkIfJobExists(testJobRequest));

        mvc.perform(post(BASE_URL + "/schedule-job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testJobRequest)))
                .andExpect(status().isOk()).andReturn();

        assertTrue(schedulerService.checkIfJobExists(testJobRequest));
    }

    @Test
    @SneakyThrows
    void unscheduleJobs() {
        QuartzRequest testJobRequest = new QuartzRequest(SchedulerJob.TEST_JOB);

        schedulerService.scheduleJob(testJobRequest);

        mvc.perform(post(BASE_URL + "/unschedule-jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(testJobRequest))))
                .andExpect(status().isOk()).andReturn();

        assertFalse(schedulerService.checkIfJobExists(testJobRequest));
    }
}
