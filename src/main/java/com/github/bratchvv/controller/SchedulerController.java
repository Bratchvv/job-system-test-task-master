package com.github.bratchvv.controller;


import com.github.bratchvv.controller.dto.QuartzRequest;
import com.github.bratchvv.service.SchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Scheduler API controller.
 *
 * @author Vladimir Bratchikov
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = {"Scheduler API"})
@RequestMapping(value = "/v1/scheduler")
public class SchedulerController {

    private final SchedulerService schedulerService;

    @ApiOperation("Check if scheduled job exists")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error.")})
    @PostMapping(value = "/check-job")
    public Boolean checkIfJobExists(@Valid @RequestBody QuartzRequest request) {
        log.debug("Checking if job {} exists", request.getJob());
        return schedulerService.checkIfJobExists(request);
    }

    @ApiOperation("Schedule all jobs")
    @PostMapping("/schedule-all-jobs")
    public ResponseEntity<?> scheduleAllJobs() {
        log.debug("Starting to schedule all jobs");
        return schedulerService.scheduleAllJobs();
    }

    @ApiOperation("Schedule particular job from request")
    @PostMapping("/schedule-job")
    public ResponseEntity<?> scheduleJob(@RequestBody QuartzRequest request) {
        log.debug("Start to scheduling jobs {}", request.getJob());
        return schedulerService.scheduleJob(request);
    }

    @ApiOperation("Unschedule all jobs from request")
    @PostMapping("/unschedule-jobs")
    public ResponseEntity<?> unscheduleJobs(@RequestBody List<QuartzRequest> request) {
        log.debug("Unscheduling jobs from request {}", request);
        return schedulerService.unscheduleJobs(request);
    }
}
