package com.github.bratchvv.controller;

import com.github.bratchvv.controller.dto.QuartzCronRequest;
import com.github.bratchvv.controller.dto.QuartzRequest;
import com.github.bratchvv.service.GeneralJobService;
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

/**
 * Jobs API controller.
 *
 * @author Vladimir Bratchikov
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = {"Quartz API"})
@RequestMapping(value = "/v1/quartz")
public class QuartzController {

    private final GeneralJobService jobService;

    @ApiOperation("Execute scheduled job")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error.")})
    @PostMapping(value = "/execute/job")
    public ResponseEntity<?> executeScheduledJob(@Valid @RequestBody QuartzRequest request) {
        log.debug("executing job {}", request.getJob());
        return jobService.executeJob(request);
    }

    @ApiOperation("Pause scheduled job")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error.")})
    @PostMapping(value = "/pause/job")
    public ResponseEntity<?> pauseScheduledJob(@Valid @RequestBody QuartzRequest request) {
        log.debug("Pausing job {}", request.getJob());
        return jobService.pauseRunningJob(request);
    }

    @ApiOperation("Resume paused job")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error.")})
    @PostMapping(value = "/resume/job")
    public ResponseEntity<?> resumePausedJob(@Valid @RequestBody QuartzRequest request) {
        log.debug("Resuming paused job {}", request.getJob());
        return jobService.resumePausedJob(request);
    }

    @ApiOperation("Change cron expression for job")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error.")})
    @PostMapping(value = "/change-cron/job")
    public ResponseEntity<?> changeCronExpression(@Valid @RequestBody QuartzCronRequest request) {
        log.debug("Changing cron expression {} for job {}",
                request.getCronExpression(), request.getQuartzRequest().getJob());
        return jobService.updateCronExpressionForJob(request.getQuartzRequest(), request.getCronExpression());
    }
}
