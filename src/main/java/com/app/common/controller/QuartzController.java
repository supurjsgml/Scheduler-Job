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
	public String rescheduleJob(String triggerName, String groupName, String jobName, String cron)
			throws SchedulerException {
		quartzService.rescheduleJob(triggerName, groupName, jobName, cron);
		return "Job rescheduled to: " + cron;
	}

	@PostMapping("/login")
	public Map<String, Object> login(@RequestBody MemberReqDTO memberReqDTO) {
		HashMap<String, Object> result = new HashMap<>();

		try {
			// 먼저 이력서 갱신 및 로그인 검증을 1회 실행
			updater.updateResume(memberReqDTO);

			// 검증 성공 시 메모리 유저 정보 등록 및 스케줄러 등록
			jobKoreaRegistryService.registerUser(memberReqDTO);

			String userId = memberReqDTO.getId();
			Map<String, Object> jobDataMap = new HashMap<>();
			jobDataMap.put("userId", userId);

			// 유저용 잡 등록 (최초 실행은 30분 뒤부터)
			quartzService.registerJob(userId, QuartzService.QUARTZ_GROUP_NAME, JobKoreaUserResumeJob.class, userId,
					null, 30 * 60 * 1000L, jobDataMap);

			// 토큰생성
			memberReqDTO.setToken("test");
			result.put("token", memberReqDTO.getToken());
		} catch (Exception e) {
			String errMsg = "서버에러 발생 관리자에게 문의해 주세요.";
			String errCode = "500";

			if (e.getMessage() != null && (e.getMessage().contains("no such element") || e.getMessage().contains("Unable to locate element")
					|| e.getMessage().contains("unexpected alert open"))) {
				errCode = "9000";
				errMsg = "로그인에 실패 하였습니다. 아이디 비밀번호를 확인해 주세요.";
			}

			result.put("errMsg", errMsg);
			result.put("errCode", errCode);
			log.error("로그인 및 이력서 갱신 실패 : {}", e.getMessage());
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