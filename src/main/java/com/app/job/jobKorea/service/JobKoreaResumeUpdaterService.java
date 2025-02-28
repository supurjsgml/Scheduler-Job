package com.app.job.jobKorea.service;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.app.job.dto.req.MemberReqDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobKoreaResumeUpdaterService {
	
	private final WebDriver driver;
	
    public void updateResume(MemberReqDTO memberReqDTO) throws Exception {
//        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe");

        WebDriverWait wait = null;

        try {
            driver.get("https://www.jobkorea.co.kr/Login/Login_Tot.asp");
            log.info("✅ JobKorea 홈페이지 접속 완료");

            // 로그인
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement id = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("M_ID")));
            WebElement pw = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("M_PWD")));
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));

            id.sendKeys(memberReqDTO.getId());
            pw.sendKeys(memberReqDTO.getPw());
            loginButton.click();

            // 로그인 성공 여부 확인
            Thread.sleep(3000); // 로그인 요청 후 일정 시간 대기 (네트워크 지연 대응)

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
