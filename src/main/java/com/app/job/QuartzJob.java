package com.app.job;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.app.job.dto.req.MemberReqDTO;
import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuartzJob extends QuartzJobBean {

	private final JobKoreaResumeUpdaterService updater;
	
    @Override
    protected void executeInternal(JobExecutionContext context){
        log.info("이력서 갱신 작업 실행 중...");
        try {
			updater.updateResume(MemberReqDTO.builder().id("supurjsgml").pw("!sotusjf0").build());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}