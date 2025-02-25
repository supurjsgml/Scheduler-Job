package com.app.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

public class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory {
    private AutowireCapableBeanFactory beanFactory;

    public void setAutowireCapableBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        // Quartz가 Job 인스턴스를 생성
        Object job = super.createJobInstance(bundle);
        // Spring의 의존성 주입 수행
        beanFactory.autowireBean(job);
        return job;
    }
}