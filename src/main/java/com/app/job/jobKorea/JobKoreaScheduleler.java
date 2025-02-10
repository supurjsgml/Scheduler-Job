package com.app.job.jobKorea;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
    @Scheduled(cron = "0 0/30 * * * ?")
    public void scheduleResumeUpdate() {
        log.info("🕒 이력서 갱신 작업 실행 중...");
        updater.updateResume();
    }
    
    //죽지마오..
    @Scheduled(cron = "0 0/10 * * * ?")
    public void stilALive() {
    	try {
    		RestTemplate rt = new RestTemplate();
    		rt.getForEntity("https://jcheduler-job-7b7308a3f9fd.herokuapp.com", null);
		} catch (Exception e) {
			log.info("저 아직 살아 있어효");
		}
    }
}
