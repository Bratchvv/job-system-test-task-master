package com.github.bratchvv.controller.dto;

import com.github.bratchvv.api.SchedulerJob;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author Vladimir Bratchikov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuartzRequest {

    @ApiModelProperty(value = "Job", required = true)
    @NotNull(message = "Job can not be null.")
    private SchedulerJob job;
}
