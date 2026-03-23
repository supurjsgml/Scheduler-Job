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
    
    public Mono<String> getAccessToken() {
        return webClient.post()
                .uri(restApiProperties.kakao().auth().token())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("refresh_token", refreshToken))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .doOnError(e -> log.error("카카오 토큰 갱신 실패: {}", e.getMessage()));
    }
    
    public void sendKakao(String msg) {
        getAccessToken().flatMap(accessToken -> {
            try {
            	//카톡text 템플릿 생성
                KakaoTextTemplate template = KakaoTextTemplate.restartTemplate(msg, restApiProperties.batch().baseUrl());
                
                //제이슨 ~ 직여을화
                String templateJson = objectMapper.writeValueAsString(template);

                //파람이 태어났어요
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("template_object", templateJson);

                //카톡 나에게 메세지 전송
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
            err -> log.error("카카오 알림 전송 최종 실패: {}", err.getMessage())
        );
    }
}