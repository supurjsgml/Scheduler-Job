package com.app.job.jobKorea;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobKoreaScheduleler {
    private final JobKoreaResumeUpdaterService updater;

    public JobKoreaScheduleler(JobKoreaResumeUpdaterService updater) {
        this.updater = updater;
    }

    //매일 1시간마다 실행
    @Scheduled(cron = "0 0/10 * * * ?")
    public void scheduleResumeUpdate() {
        log.info("🕒 이력서 갱신 작업 실행 중...");
        updater.updateResume();
    }
}
