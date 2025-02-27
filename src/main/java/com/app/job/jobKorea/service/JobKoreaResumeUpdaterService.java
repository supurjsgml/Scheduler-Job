package com.app.job.jobKorea.service;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.app.job.dto.req.MemberReqDTO;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Component
@Slf4j
public class JobKoreaResumeUpdaterService {
    public void updateResume(MemberReqDTO memberReqDTO) throws Exception {
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.get("https://www.jobkorea.co.kr/Login/Login_Tot.asp");
            log.info("✅ JobKorea 홈페이지 접속 완료");

            // 로그인
            WebElement id = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("M_ID")));
            WebElement pw = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("M_PWD")));
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));

            id.sendKeys(memberReqDTO.getId());
            pw.sendKeys(memberReqDTO.getPw());
            loginButton.click();

            // 로그인 성공 여부 확인
//            Thread.sleep(3000); // 로그인 요청 후 일정 시간 대기 (네트워크 지연 대응)
            log.info("✅ 로그인 성공!");

            WebElement linkElement = driver.findElement(By.cssSelector(".status a"));
            driver.get(linkElement.getAttribute("href"));

            WebElement updateButton = driver.findElement(By.className("button-update"));
            updateButton.click();
            log.info("✅ 이력서 갱신 완료");

        } catch (Exception e) {
            log.error("❌ 오류 발생: {}", e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            log.info("✅ 크롬 드라이버 종료");
            driver.quit();
        }
    }
}
