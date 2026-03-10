package com.app.job.jobKorea.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.app.job.jobKorea.dto.req.MemberReqDTO;
import com.app.util.HerokuRestarter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobKoreaResumeUpdaterService {
	
	public Map<String, Object> updateResumeLogin(MemberReqDTO memberReqDTO) {
		HashMap<String, Object> result = new HashMap<>();
		try {
			updateResume(memberReqDTO);
		} catch (Exception e) {
			String errMsg = "서버에러 발생 관리자에게 문의해 주세요.";
			String errCode = "500";
			
			if (e.getMessage().contains("no such element") || e.getMessage().contains("Unable to locate element") || e.getMessage().contains("unexpected alert open")) {
				errCode = "9000";
				errMsg = "로그인에 실패 하였습니다. 아이디 비밀번호를 확인해 주세요.";
			}
			
			result.put("errMsg", errMsg);
			result.put("errCode", errCode);
		
			log.error(e.getMessage());
		}
		
		return result;
		
	}
	
    public void updateResume(MemberReqDTO memberReqDTO) {
    	ChromeOptions options = null;
    	WebDriver driver = null;
    	WebDriverWait wait = null;
    	
        try {
        	options = new ChromeOptions();
        	options.addArguments("--headless");                    // GUI 없이 실행 (서버 환경 필수)
        	options.addArguments("--no-sandbox");                  // 보안 정책 우회 (메모리 절약)
        	options.addArguments("--disable-dev-shm-usage");       // 공유 메모리 비활성화 (Heroku 필수)
        	options.addArguments("--disable-gpu");                 // GPU 사용 비활성화
        	options.addArguments("--remote-allow-origins=*");      // 원격 실행 허용
        	options.addArguments("--disable-extensions");          // 확장 프로그램 비활성화
        	options.addArguments("--remote-debugging-pipe"); 	   // 소켓 대신 파이프 통신 사용
        	options.addArguments("--disable-software-rasterizer");
        	
        	driver = new ChromeDriver(options);
        	wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        	
            driver.get("https://www.jobkorea.co.kr/Login/Login_Tot.asp");
            log.info("JobKorea 홈페이지 접속 완료");

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
            log.info("이력서 갱신 완료");
            
        } catch (SessionNotCreatedException e) {
        	log.error("[SessionNotCreatedException] 오류 발생 : {}", e.getMessage());
        	e.printStackTrace();
        	HerokuRestarter.restartHerokuDyno();
        } catch (Exception e) {
        	log.error("[Exception] 오류 발생 : {}", e.getMessage());
        	
        	if (e.getMessage().contains("Command failed with code: 134") || e.getMessage().contains("Unable to obtain") || e.getMessage().contains("TimeoutException")) {
        		HerokuRestarter.restartHerokuDyno();
			}
        	
        	throw new RuntimeException(e.getMessage());
        } finally {
            if (driver != null) {
                log.info("크롬 드라이버 종료");
                driver.quit();
            }
        }
    }
}
