package com.github.bratchvv;

import com.github.bratchvv.api.SchedulerJob;
import com.github.bratchvv.entity.JobInstance;
import com.github.bratchvv.entity.Status;
import com.github.bratchvv.repository.JobInstanceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MongoDbContainerTest extends BaseSpringBootTest {

    @Autowired
    private JobInstanceRepository jobInstanceRepository;

    @AfterEach
    void cleanUp() {
        this.jobInstanceRepository.deleteAll();
    }

    @Test
    void shouldReturnListOfCustomerWithMatchingRate() {
        JobInstance jobInstance = new JobInstance();
        jobInstance.setJobKey(SchedulerJob.DAILY_JOB.name());
        jobInstance.setStatus(Status.NEW);
        jobInstance.setStartTime(LocalDateTime.now());
        jobInstanceRepository.save(jobInstance);
        assertEquals(1, jobInstanceRepository.findAll().size());
    }
}