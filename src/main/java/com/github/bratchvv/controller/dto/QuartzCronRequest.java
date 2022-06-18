package com.github.bratchvv.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Vladimir Bratchikov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuartzCronRequest {

    @ApiModelProperty(value = "Job", required = true)
    @NotNull(message = "Job can not be null.")
    private QuartzRequest quartzRequest;

    @ApiModelProperty(value = "cron expression", required = true)
    @NotBlank(message = "Cron expression can not be null")
    private String cronExpression;
}
