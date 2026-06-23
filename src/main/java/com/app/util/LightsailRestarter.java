package com.app.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LightsailRestarter {

    public static void restartPm2Process() {
        log.info("라이트세일 PM2 프로세스(scheduler-job) 재시작 실행...");
        try {
            // 리눅스 쉘 환경에서 pm2 restart 명령 실행
            String[] cmd = {"/bin/sh", "-c", "pm2 restart scheduler-job"};
            Runtime.getRuntime().exec(cmd);
            log.info("PM2 재시작 명령 전송 완료");
        } catch (Exception e) {
            log.error("PM2 프로세스 재시작 실패: {}", e.getMessage());
        }
    }
}
