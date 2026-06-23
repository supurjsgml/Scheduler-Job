package com.app.kakao.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.app.common.constants.RestApiProperties;
import com.app.common.utils.CommonUtil;
import com.app.kakao.template.KakaoTextTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAlarmService {

    private static final ObjectMapper objectMapper = CommonUtil.om;

    private final WebClient webClient;

    private final RestApiProperties restApiProperties;

    @Value("${key.kakao.clientId}")
    private String clientId;

    @Value("${key.kakao.clientSecret}")
    private String clientSecret;

    @Value("${key.kakao.refreshToken}")
    private String refreshToken;

    private String loadRefreshToken() {
        try {
            java.io.File file = new java.io.File("kakao-token.txt");
            if (file.exists()) {
                String fileToken = java.nio.file.Files.readString(file.toPath()).trim();
                if (!fileToken.isEmpty()) {
                    log.info("로컬 파일(kakao-token.txt)에서 카카오 리프레시 토큰을 로드했습니다.");
                    return fileToken;
                }
            }
        } catch (Exception e) {
            log.error("로컬 파일에서 리프레시 토큰을 읽는 도중 오류 발생: {}", e.getMessage());
        }
        log.info("설정 정보(환경변수)의 카카오 리프레시 토큰을 사용합니다.");
        return refreshToken;
    }

    private void saveRefreshToken(String newToken) {
        try {
            java.io.File file = new java.io.File("kakao-token.txt");
            java.nio.file.Files.writeString(file.toPath(), newToken.trim());
            log.info("새로운 카카오 리프레시 토큰을 로컬 파일(kakao-token.txt)에 저장했습니다.");
        } catch (Exception e) {
            log.error("새로운 리프레시 토큰을 로컬 파일에 저장하는 도중 오류 발생: {}", e.getMessage());
        }
    }

    // 7일마다 주기적으로 카카오 토큰을 자동 갱신해 주어 리프레시 토큰이 만료(2달)되는 것을 영구히 방지합니다.
    @Scheduled(cron = "0 0 4 */7 * ?") // 7일마다 새벽 4시 실행
    public void scheduledTokenRefresh() {
        log.info("정기 카카오 토큰 자동 갱신 스케줄러 작동 시작");
        getAccessToken().subscribe(
            res -> log.info("정기 카카오 토큰 자동 갱신 성공"),
            err -> {
                log.error("정기 카카오 토큰 자동 갱신 실패: {}", err.getMessage());
                sendKakao("정기 카카오 토큰 자동 갱신 실패: " + err.getMessage());
            }
        );
    }

    public Mono<String> getAccessToken() {
        String currentRefreshToken = loadRefreshToken();
        return webClient.post()
                .uri(restApiProperties.kakao().auth().token())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("refresh_token", currentRefreshToken))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String accessToken = (String) response.get("access_token");
                    String newRefreshToken = (String) response.get("refresh_token");
                    if (newRefreshToken != null && !newRefreshToken.trim().isEmpty() && !newRefreshToken.equals(currentRefreshToken)) {
                        saveRefreshToken(newRefreshToken);
                    }
                    return Mono.just(accessToken);
                })
                .doOnError(e -> log.error("카카오 토큰 갱신 실패: {}", e.getMessage()));
    }

    public void sendKakao(String msg) {
        getAccessToken().flatMap(accessToken -> {
            try {
                // 카톡text 템플릿 생성
                KakaoTextTemplate template = KakaoTextTemplate.restartTemplate(msg,
                        restApiProperties.batch().baseUrl());

                // 제이슨 ~ 직여을화
                String templateJson = objectMapper.writeValueAsString(template);

                // 파람이 태어났어요
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("template_object", templateJson);

                // 카톡 나에게 메세지 전송
                return webClient.post()
                        .uri(restApiProperties.kakao().api().memo())
                        .header("Authorization", "Bearer ".concat(accessToken))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(BodyInserters.fromFormData(formData))
                        .retrieve()
                        .bodyToMono(String.class);

            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        })
                .subscribe(
                        res -> log.info("카카오 알림 전송 성공 : {}", res),
                        err -> log.error("카카오 알림 전송 최종 실패: {}", err.getMessage()));
    }
}