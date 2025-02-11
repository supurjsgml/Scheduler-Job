package com.app.job.jobKorea.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobKoreaResumeUpdaterService {
    public void updateResume() {
//        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe");

    	ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); 				                //GUI 없이 실행 (필수)
        options.addArguments("--no-sandbox"); 				                //보안 정책 우회 (메모리 절약)
        options.addArguments("--disable-dev-shm-usage"); 	                //공유 메모리 비활성화 (Heroku 필수)
        options.addArguments("--disable-gpu"); 				                //GPU 사용 비활성화
        options.addArguments("--remote-allow-origins=*"); 	                //원격 실행 허용
        options.addArguments("--disable-background-timer-throttling"); 		//백그라운드 작업 최소화
        options.addArguments("--disable-backgrounding-occluded-windows"); 	//백그라운드 윈도우 최소화
        options.addArguments("--disable-extensions"); 						//확장 프로그램 비활성화

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://www.jobkorea.co.kr/Login/Login_Tot.asp");
            log.info("✅ JobKorea 홈페이지 접속 완료");

            // 로그인
            WebElement id = driver.findElement(By.name("M_ID"));
            WebElement pw = driver.findElement(By.name("M_PWD"));
            WebElement loginButton = driver.findElement(By.className("login-button"));

            id.sendKeys("supurjsgml");
            pw.sendKeys("!sotusjf0");
            loginButton.click();

            Thread.sleep(3000); // 로그인 후 대기

            // 이력서 갱신 버튼 클릭
            driver.get("https://www.jobkorea.co.kr/User/Resume/View?rNo=19402578");
            WebElement updateButton = driver.findElement(By.className("button-update"));
            updateButton.click();

            log.info("✅ 이력서 갱신 완료");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != driver) {
            	log.info("크롬 드라이버 초기화: {}", driver);
            	driver.quit();
            }
        }
    }
}

