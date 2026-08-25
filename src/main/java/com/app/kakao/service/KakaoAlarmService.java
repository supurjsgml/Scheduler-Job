package com.app.kakao.service;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
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

    // 7일마다 새벽 4시 실행
    public void scheduledTokenRefresh() {
        log.info("정기 카카오 토큰 자동 갱신 스케줄러 작동 시작");

        webClientUtil.postAsync(
                restApiProperties.guney().baseUrl() + "/api/kakao/refresh-token",
                Map.of(),
                Map.class
        ).subscribe(
                res -> {
                    log.info("정기 카카오 토큰 자동 갱신 응답 수신: {}", res);

                    if (ObjectUtils.isNotEmpty(res)) {
                        if (ObjectUtils.isNotEmpty(res.get("header")) && "BAD_REQUEST".equals(((Map<String, Object>) res.get("header")).get("code"))) {
                            log.warn("카카오 토큰 갱신 응답 오류: {}", ((Map<String, Object>) res.get("header")).get("message"));
                            return;
                        }
                        log.info("카카오 토큰 자동 갱신 처리 완료");
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

    public void sendKakao(String msg) {
        webClientUtil.postAsync(
                restApiProperties.guney().baseUrl() + "/api/kakao/send",
                Map.of("msg", msg != null ? msg : "알림 내용 없음"),
                Map.class
        ).subscribe(
                res -> log.info("카카오 알림 전송 성공"),
                err -> log.error("카카오 알림 전송 최종 실패: {}", err.getMessage()));
    }
}