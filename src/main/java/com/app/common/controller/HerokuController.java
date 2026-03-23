package com.app.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.util.HerokuRestarter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/heroku")
@RequiredArgsConstructor
@Slf4j
public class HerokuController {

    @DeleteMapping
    public HttpStatus restartHeroku() {
    	HerokuRestarter.restartHerokuDyno();
		return HttpStatus.OK;
    }

}