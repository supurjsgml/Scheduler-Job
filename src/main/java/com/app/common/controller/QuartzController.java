package com.app.common.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.job.jobKorea.JobKoreaUserResumeJob;
import com.app.job.jobKorea.dto.req.MemberReqDTO;
import com.app.job.jobKorea.service.JobKoreaRegistryService;
import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;
import com.app.job.quartz.dto.res.QuartzLiveJobsResponseDto;
import com.app.job.quartz.service.QuartzService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/quartz")
@RequiredArgsConstructor
@Slf4j
public class QuartzController {

    private final QuartzService quartzService;
    
    private final JobKoreaResumeUpdaterService updater;
    
    private final JobKoreaRegistryService jobKoreaRegistryService;

    @PostMapping("/pause")
    public String pauseJob(String triggerName, String groupName) throws SchedulerException {
        quartzService.pauseJob(triggerName, groupName);
        return "Job paused: " + triggerName;
    }

    @PostMapping("/resume")
    public String resumeJob(String triggerName, String groupName) throws SchedulerException {
        quartzService.resumeJob(triggerName, groupName);
        return "Job resumed: " + triggerName;
    }

    @PostMapping("/reschedule")
    public String rescheduleJob(String triggerName, String groupName, String jobName, String cron) throws SchedulerException {
        quartzService.rescheduleJob(triggerName, groupName, jobName, cron);
        return "Job rescheduled to: " + cron;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody MemberReqDTO memberReqDTO) {
    	HashMap<String, Object> result = new HashMap<>();

    	try {
    		//난 저장소가 없어요
    		jobKoreaRegistryService.registerUser(memberReqDTO);
    		
    		String userId = memberReqDTO.getId();
    		
    		Map<String, Object> jobDataMap = new HashMap<>();
    		jobDataMap.put("userId", userId);
    		
    		//유저용 잡 등록
			quartzService.registerJob(userId, userId.concat(QuartzService.GROUP_NAME), JobKoreaUserResumeJob.class, userId, "* 0/30 * * * ?", jobDataMap);
		} catch (SchedulerException e) {
			log.error("JobKoreaRegistryService 잡등록 터졌어 ERROR : ");
			e.printStackTrace();
		}
    	
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
    
    @GetMapping("/stil/alive")
    public List<QuartzLiveJobsResponseDto> getAllJobs() {
    	return quartzService.getAllJobs();
    }
    
    @GetMapping("/user/job")
    public QuartzLiveJobsResponseDto getUserJobStatus(@RequestParam(required = true) String userId) {
    	return quartzService.getUserJobStatus(userId);
    }
}