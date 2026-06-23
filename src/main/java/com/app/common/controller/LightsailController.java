package com.app.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.util.LightsailRestarter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/lightsail")
@RequiredArgsConstructor
@Slf4j
public class LightsailController {

    @GetMapping("/restart")
    public HttpStatus restartScheduler() {
        LightsailRestarter.restartPm2Process();
        return HttpStatus.OK;
    }
}
