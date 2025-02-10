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

    // 30분마다 실행 (초 분 시 * * * = 0 30 * * * *)
    @Scheduled(cron = "0 0 * * * *")
    public void scheduleResumeUpdate() {
        log.info("🕒 이력서 갱신 작업 실행 중...");
        updater.updateResume();
    }
}
