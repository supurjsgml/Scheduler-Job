package com.app.quartzListener;

import io.micrometer.core.instrument.Counter;  
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;

import org.quartz.*;  

@AllArgsConstructor
public class JobExecutionMetricsListener implements JobListener {  

    private final MeterRegistry registry;  

    @Override  
    public String getName() {  
        return "metricsListener";  
    }  

    @Override  
    public void jobToBeExecuted(JobExecutionContext context) {}  

    @Override  
    public void jobExecutionVetoed(JobExecutionContext context) {}  

    @Override  
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {  
        String jobName = context.getJobDetail().getKey().getName();  
        String status  = jobException == null ? "success" : "failed";  

        Counter.builder("quartz.job.executed")  
            .tag("job", jobName)  
            .tag("status", status)  
            .register(registry)  
            .increment();  
    }  
}  