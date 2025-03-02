package com.app.job.controller;

import java.util.HashMap;
import java.util.Map;

import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.job.dto.req.MemberReqDTO;
import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;
import com.app.job.jobKorea.service.QuartzService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/quartz")
@RequiredArgsConstructor
@Slf4j
public class QuartzController {

    private final QuartzService quartzService;
    
    private final JobKoreaResumeUpdaterService updater;

    @PostMapping("/pause")
    public String pauseJob(@RequestParam("triggerName") String triggerName) throws SchedulerException {
        quartzService.pauseJob(triggerName);
        return "Job paused: " + triggerName;
    }

    @PostMapping("/resume")
    public String resumeJob(@RequestParam("triggerName") String triggerName) throws SchedulerException {
        quartzService.resumeJob(triggerName);
        return "Job resumed: " + triggerName;
    }

    @PostMapping("/reschedule")
    public String rescheduleJob(@RequestParam("triggerName") String triggerName, @RequestParam("jobName") String jobName, 
    		@RequestParam("cron") String cron) throws SchedulerException {
        quartzService.rescheduleJob(triggerName, jobName, cron);
        return "Job rescheduled to: " + cron;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody MemberReqDTO memberReqDTO) {
    	HashMap<String, Object> result = new HashMap<>();

    	try {
    		updater.updateResume(memberReqDTO);
    		//토큰생성
    		memberReqDTO.setToken("test");
    		result.put("token", memberReqDTO.getToken());
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
			e.getStackTrace();
		}
    	return result;
    }
}