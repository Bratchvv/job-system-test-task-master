package com.github.bratchvv.config;

import lombok.RequiredArgsConstructor;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Global quartz configuration.
 *
 * @author Vladimir Bratchikov
 * @see QuartzJobBean
 */
@Configuration
@RequiredArgsConstructor
public class QuartzConfiguration {

    private final ApplicationContext applicationContext;

    @Value("${com.github.bratchvv.quartz.quartzConfigFile:#{null}}")
    private String quartzConfigFile;

    /**
     * Here we integrate quartz with Spring and let Spring manage initializing
     * quartz as a spring bean.
     *
     * @return an instance of {@link SchedulerFactoryBean} which will be managed
     * by spring.
     */
    @Bean
    @ConditionalOnProperty(name = "com.github.bratchvv.quartz.quartzConfigFile")
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();

        scheduler.setAutoStartup(true);
        scheduler.setOverwriteExistingJobs(true);
        scheduler.setJobFactory(jobFactory());

        scheduler.setApplicationContextSchedulerContextKey("applicationContext");
        ClassPathResource configLocation = new ClassPathResource(quartzConfigFile);
        if (configLocation.exists()) {
            scheduler.setConfigLocation(configLocation);
        }
        scheduler.setWaitForJobsToCompleteOnShutdown(true);
        return scheduler;
    }

    /**
     * Create the job factory bean
     *
     * @return Job factory bean
     */
    @Bean
    public JobFactory jobFactory() {
        ApplicationContextHolder jobFactory = new ApplicationContextHolder();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

}
