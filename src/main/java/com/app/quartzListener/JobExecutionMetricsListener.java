package com.app.quartzListener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;  

@RequiredArgsConstructor
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
            .tag("job_name", jobName)  
            .tag("status", status)  
            .register(registry)  
            .increment();  
    }  
}  