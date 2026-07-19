package com.app.job;

import org.springframework.stereotype.Component;

import com.app.job.quartz.QuartzJob;
import com.app.job.quartz.service.QuartzService;
import com.app.job.stilALive.StilALiveJob;
import com.app.kakao.job.KakaoJob;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuartzJobInitializer {

    private final QuartzService quartzService;

    @PostConstruct
    public void initJobs() {
        try {
            // 이력서 갱신 잡
            // quartzService.registerJob("QuartzJob", QuartzService.QUARTZ_GROUP_NAME, QuartzJob.class, "jobKoreaResumeUpdate", "0 0/10 * * * ?");
            
            // KeepAlive 잡
            // quartzService.registerJob("StilALiveJob", QuartzService.QUARTZ_GROUP_NAME, StilALiveJob.class, "StilALiveJob", "0 0/15 * * * ?");
            
            // 카카오 토큰 갱신 잡
            quartzService.registerJob("KakaoJob", QuartzService.QUARTZ_GROUP_NAME, KakaoJob.class, "kakaoTokenRefresh", "0 0 4 */7 * ?");
            
            log.info("초기 Quartz Job 등록 프로세스 완료");
        } catch (Exception e) {
            log.error("초기 Quartz Job 등록 중 오류 발생", e);
        }
    }
}
