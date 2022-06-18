package com.github.bratchvv.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;


/**
 * Context holder for using spring context in quartz jobs.
 *
 * @author Vladimir Bratchikov
 * @see QuartzJobBean
 */
@Component
public final class ApplicationContextHolder extends SpringBeanJobFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    private transient AutowireCapableBeanFactory beanFactory;

    public static ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        beanFactory = ctx.getAutowireCapableBeanFactory();
        context = ctx;
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }
}