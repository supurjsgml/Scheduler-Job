package com.app.job.jobKorea;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.app.job.jobKorea.dto.req.MemberReqDTO;
import com.app.job.jobKorea.service.JobKoreaRegistryService;
import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JobKoreaUserResumeJob extends QuartzJobBean {
	
	private final JobKoreaRegistryService jobKoreaRegistryService;
    private final JobKoreaResumeUpdaterService jobKoreaResumeUpdaterService;
    
    @Override
    protected void executeInternal(JobExecutionContext context) {
        //JobDataMap에서 userId를 꺼냄
        String userId = context.getJobDetail().getJobDataMap().getString("userId");
        
        if (StringUtils.isBlank(userId)) {
            log.error("스케줄러 작업 실행 실패: JobDataMap에서 userId를 찾을 수 없습니다.");
            return;
        }
        
        log.info("JobKoreaUserResumeJob 이력서 갱신 작업 실행 중... 대상 ID : {}", userId);
        
        //서비스에서 메모리(userStore)에 있는 유저 정보를 조회
        MemberReqDTO userData = jobKoreaRegistryService.getUser(userId);
        
        if (ObjectUtils.isNotEmpty(userData)) {
            jobKoreaResumeUpdaterService.updateResume(userData);
        } else {
            log.error("메모리에 해당 유저 정보가 없습니다. ID : {}", userId);
        }
    }
}
