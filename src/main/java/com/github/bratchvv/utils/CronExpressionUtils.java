package com.github.bratchvv.utils;

import lombok.experimental.UtilityClass;
import org.quartz.CronExpression;

@UtilityClass
public class CronExpressionUtils {

    /**
     * Validate cron expression.
     *
     * @param cronExpression cron expression
     */
    public static void validateExpression(String cronExpression) {
        try {
            CronExpression.validateExpression(cronExpression);
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format("Cron expression %s is not valid, because %s", cronExpression, e)
            );
        }
    }
}
