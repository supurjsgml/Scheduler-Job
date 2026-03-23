package com.app.job.jobKorea;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.app.job.jobKorea.dto.req.MemberReqDTO;
import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobKoreaScheduleler {

	private final JobKoreaResumeUpdaterService updater;
    
    private final RestTemplate restTemplate;

    //매일 1시간마다 실행
//    @Scheduled(cron = "0 0/30 * * * ?")
    public void scheduleResumeUpdate() {
        log.info("이력서 갱신 작업 실행 중...");
        try {
			updater.updateResume(MemberReqDTO.builder().id("").pw("").build());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    //죽지마오..
//    @Scheduled(cron = "0 0/10 * * * ?")
    public void stilALive() {
    	try {
    		restTemplate.getForEntity("https://jcheduler-job-7b7308a3f9fd.herokuapp.com", null);
		} catch (Exception e) {
			log.info("저 아직 살아 있어효");
		}
    }
    
//    //죽지마오2..
//    @Scheduled(cron = "0 2 * * * ?")
//    public void 죽지마요ㅠㅠ() {
//    	log.info("메모리를 살려주오 ~!");
//    }
    
//    //죽지마오3..
//    @Scheduled(cron = "0 0 7 * * ?")
//    public void 죽지마요ㅠㅠㅜ() {
//    	log.info("🛑 애플리케이션 종료 시도...");
//        try {
//            Runtime.getRuntime().exec("kill 1");
//        } catch (IOException e) {
//            log.error("❌ 종료 실패: ", e);
//        }
//    }
}
