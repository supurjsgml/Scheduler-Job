package com.app.job.jobKorea;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobKoreaScheduleler {

	private final JobKoreaResumeUpdaterService updater;
    
    private final WebClient webClient;


    //매일 1시간마다 실행
    @Scheduled(cron = "0 0/30 * * * ?")
    public void scheduleResumeUpdate() {
        log.info("🕒 이력서 갱신 작업 실행 중...");
        updater.updateResume();
    }
    
    //죽지마오..
    @Scheduled(cron = "0 0/10 * * * ?")
    public void stilALive() {
    	 webClient.get()
         .uri("https://jcheduler-job-7b7308a3f9fd.herokuapp.com")
         .retrieve()
         .bodyToMono(String.class)
         .doOnSuccess(response -> log.info("✅ Heroku Keep-Alive 성공"))
         .doOnError(error -> log.info("저 아직 살아 있어효 : {}", error.getMessage()))
         .subscribe();
    }
    
    //죽지마오2..
    @Scheduled(cron = "2 1 * * * ?")
    public void 죽지마요ㅠㅠ() {
    	System.gc();
    }
}
