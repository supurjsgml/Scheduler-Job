package com.app.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class StilALiveJob extends QuartzJobBean {

    private final RestTemplate restTemplate;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("🕒 StilALiveJob 작업 실행 중...");
        
        try {
            restTemplate.getForEntity("https://jcheduler-job-7b7308a3f9fd.herokuapp.com", null);
        } catch (Exception e) {
            log.info("저 아직 살아 있어효");
        }
    }
}