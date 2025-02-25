package com.app.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuartzJob extends QuartzJobBean {

	private final JobKoreaResumeUpdaterService updater;
	
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("🕒 이력서 갱신 작업 실행 중...");
        updater.updateResume();
        System.gc();
    }

}