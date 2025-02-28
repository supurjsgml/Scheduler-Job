package com.app.job.jobKorea.service;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.app.job.dto.req.MemberReqDTO;
import com.app.util.HerokuRestarter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobKoreaResumeUpdaterService {
	
    public void updateResume(MemberReqDTO memberReqDTO) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");                    // GUI 없이 실행 (서버 환경 필수)
        options.addArguments("--no-sandbox");                  // 보안 정책 우회 (메모리 절약)
        options.addArguments("--disable-dev-shm-usage");       // 공유 메모리 비활성화 (Heroku 필수)
        options.addArguments("--disable-gpu");                 // GPU 사용 비활성화
        options.addArguments("--remote-allow-origins=*");      // 원격 실행 허용
        options.addArguments("--disable-extensions");          // 확장 프로그램 비활성화

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

            // 로그인 성공 여부 확인 (Thread.sleep 제거, WebDriverWait 사용)
            WebElement linkElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".status a")));
            driver.get(linkElement.getAttribute("href"));

            WebElement updateButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("button-update")));
            updateButton.click();
            log.info("✅ 이력서 갱신 완료");
            
        } catch (TimeoutException e) {
            log.error("❌ [TimeoutException] 요소를 찾는 중 시간이 초과됨: {}", e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("❌ [NoSuchElementException] 요소를 찾을 수 없음: {}", e.getMessage());
        } catch (WebDriverException e) {
            log.error("❌ [WebDriverException] WebDriver 세션 오류 발생: {}", e.getMessage());
            HerokuRestarter.restartHerokuDyno();
        } catch (Exception e) {
            log.error("❌ [Exception] 기타 오류 발생: {}", e.getMessage());
        } finally {
            if (driver != null) {
                log.info("✅ 크롬 드라이버 종료");
                driver.quit();
            }
        }
    }
}
