package com.app.kakao.job;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.app.kakao.service.KakaoAlarmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoJob extends QuartzJobBean {

    private final KakaoAlarmService kakaoAlarmService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("KakaoJob 카카오 토큰 갱신 작업 실행 중...");
        kakaoAlarmService.scheduledTokenRefresh();
    }
}
