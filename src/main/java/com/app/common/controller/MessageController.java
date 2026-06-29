package com.app.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.kakao.service.KakaoAlarmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final KakaoAlarmService kakaoAlarmService;

    @GetMapping("/kakao")
    public HttpStatus sendKakaoMessage(@RequestParam String msg) {
        log.info("카카오 메시지 전송 API 호출 - 메시지: {}", msg);
        kakaoAlarmService.sendKakao(msg);
        return HttpStatus.OK;
    }

    @GetMapping("/kakao/refresh")
    public HttpStatus triggerTokenRefresh() {
        log.info("수동 카카오 토큰 자동 갱신 API 호출");
        kakaoAlarmService.scheduledTokenRefresh();
        return HttpStatus.OK;
    }
}
