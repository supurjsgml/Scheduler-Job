package com.app.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    @Bean
    WebDriver chromeDriver() {
        ChromeOptions options = new ChromeOptions();
        
        options.addArguments("--headless=new");                    // 최신 headless 모드 사용
        options.addArguments("--disable-gpu");                     // GPU 사용 비활성화
        options.addArguments("--no-sandbox");                      // 샌드박스 비활성화 (Heroku 필수)
        options.addArguments("--disable-dev-shm-usage");           // 메모리 절약 (Heroku 필수)
        options.addArguments("--disable-background-networking");   // 백그라운드 네트워크 최소화
        options.addArguments("--disable-software-rasterizer");     // GPU 렌더링 비활성화
        options.addArguments("--disable-extensions");              // 확장 프로그램 비활성화
        options.addArguments("--disable-infobars");                // 자동화 감지 UI 제거
        options.addArguments("--disable-notifications");           // 알림 비활성화
        options.addArguments("--remote-debugging-port=9222");      // 원격 디버깅 포트 지정

        return new ChromeDriver(options);
    }
}