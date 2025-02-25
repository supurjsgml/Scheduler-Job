package com.app.config;

import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    private final ApplicationContext applicationContext;

    public QuartzConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(springBeanJobFactory());
        factoryBean.setApplicationContext(applicationContext);
        factoryBean.setAutoStartup(true);
        factoryBean.setOverwriteExistingJobs(true);
        return factoryBean;
    }

    @Bean
    public AutoWiringSpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setAutowireCapableBeanFactory(applicationContext.getAutowireCapableBeanFactory());
        return jobFactory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factoryBean) throws Exception {
        return factoryBean.getObject();
    }
}