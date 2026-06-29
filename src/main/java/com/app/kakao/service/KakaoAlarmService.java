package com.app.kakao.service;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
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

        if (currentToken == null || currentToken.trim().isEmpty()) {
            log.warn("정기 카카오 토큰 자동 갱신 중단: 리프레시 토큰이 비어있음");
            return;
        }

        webClientUtil.postAsync(
                restApiProperties.guney().baseUrl() + "/api/kakao/refresh-token",
                Map.of("refreshToken", currentToken),
                Map.class
        ).subscribe(
                res -> {
                    log.info("정기 카카오 토큰 자동 갱신 응답 수신: {}", res);

                    if (ObjectUtils.isNotEmpty(res)) {
                        if (ObjectUtils.isNotEmpty(res.get("header")) && "BAD_REQUEST".equals(((Map<String, Object>) res.get("header")).get("code"))) {
                            log.info("캐캬오 ERROR : {}", ((Map<String, Object>) res.get("header")).get("message"));
                            return;
                        }

                        if (ObjectUtils.isNotEmpty(res.get("data"))) {
                            Map<String, Object> data = (Map<String, Object>) res.get("data");
                            String newRefreshToken = (String) data.get("newRefreshToken");

                            if (newRefreshToken != null && !newRefreshToken.isEmpty()) {
                                saveRefreshToken(newRefreshToken);
                            } else {
                                log.info("새로운 리프레시 토큰이 발급되지 않아 저장 단계를 건너뜁니다.");
                            }
                        }
                    } else {
                        log.info("퍽킹 없는데 ???");
                    }

                },
                err -> {
                    String errorMsg = String.format("정기 카카오 토큰 자동 갱신 실패: [%s] %s",
                            err.getClass().getSimpleName(),
                            err.getMessage() != null ? err.getMessage() : "상세 메시지 없음");
                    log.error(errorMsg, err);
                    sendKakao(errorMsg);
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
        return refreshToken != null ? refreshToken.trim() : "";
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
        String safeToken = currentToken != null ? currentToken : "";

        webClientUtil.postAsync(
                restApiProperties.guney().baseUrl() + "/api/kakao/send",
                Map.of("msg", msg != null ? msg : "알림 내용 없음", "refreshToken", safeToken),
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