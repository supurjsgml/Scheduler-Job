package com.app.kakao.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.app.common.constants.RestApiProperties;
import com.app.common.utils.WebClientUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAlarmService {

    private final WebClientUtil webClientUtil;

    private final RestApiProperties restApiProperties;

    @Value("${key.kakao.refreshToken}")
    private String refreshToken;

    // 7일마다 새벽 4시 실행
    @Scheduled(cron = "0 0 4 */7 * ?")
    public void scheduledTokenRefresh() {
        log.info("정기 카카오 토큰 자동 갱신 스케줄러 작동 시작");
        String currentToken = loadRefreshToken();

        webClientUtil.postAsync(
                restApiProperties.guney().baseUrl() + "/api/kakao/refresh-token",
                Map.of("refreshToken", currentToken),
                Map.class
        ).subscribe(
                res -> {
                    log.info("정기 카카오 토큰 자동 갱신 성공");
                    if (res != null && res.get("data") != null) {
                        Map<String, Object> data = (Map<String, Object>) res.get("data");
                        String newRefreshToken = (String) data.get("newRefreshToken");
                        if (newRefreshToken != null && !newRefreshToken.isEmpty()) {
                            saveRefreshToken(newRefreshToken);
                        }
                    }
                },
                err -> {
                    log.error("정기 카카오 토큰 자동 갱신 실패: {}", err.getMessage());
                    sendKakao("정기 카카오 토큰 자동 갱신 실패: " + err.getMessage());
                });
    }

    private String loadRefreshToken() {
        try {
            java.io.File file = new java.io.File("kakao-token.txt");
            if (file.exists()) {
                String fileToken = java.nio.file.Files.readString(file.toPath()).trim();
                if (!fileToken.isEmpty()) {
                    log.info("리프레시 토큰을 로드.");
                    return fileToken;
                }
            }
        } catch (Exception e) {
            log.error("loadRefreshToken ERROR : {}", e.getMessage());
        }
        log.info("설정 정보의 리프레시 토큰을 사용.");
        return refreshToken;
    }

    private void saveRefreshToken(String newToken) {
        try {
            java.io.File file = new java.io.File("kakao-token.txt");
            java.nio.file.Files.writeString(file.toPath(), newToken.trim());
            log.info("리프레시 토큰 저장");
        } catch (Exception e) {
            log.error("리프레시 토큰 저장중 오류 발생: {}", e.getMessage());
        }
    }

    public void sendKakao(String msg) {
        String currentToken = loadRefreshToken();

        webClientUtil.postAsync(
                restApiProperties.guney().baseUrl() + "/api/kakao/send",
                Map.of("msg", msg, "refreshToken", currentToken),
                Map.class
        ).subscribe(
                res -> {
                    log.info("카카오 알림 전송 성공");
                    if (res != null && res.get("data") != null) {
                        Map<String, Object> data = (Map<String, Object>) res.get("data");
                        String newRefreshToken = (String) data.get("newRefreshToken");
                        if (newRefreshToken != null && !newRefreshToken.isEmpty()) {
                            saveRefreshToken(newRefreshToken);
                        }
                    }
                },
                err -> log.error("카카오 알림 전송 최종 실패: {}", err.getMessage()));
    }
}