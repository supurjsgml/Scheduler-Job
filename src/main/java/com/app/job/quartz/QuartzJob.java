package com.app.job.quartz;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.app.job.jobKorea.dto.req.MemberReqDTO;
import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuartzJob extends QuartzJobBean {

	@Value("${풀어-보거라.훌랠릴}") 
    private String id;

    @Value("${풀어-보거라.랠룰}") 
    private String pw;
	
	private final JobKoreaResumeUpdaterService updater;
	
    @Override
    protected void executeInternal(JobExecutionContext context){
        log.info("이력서 갱신 작업 실행 중...");
		updater.updateResume(MemberReqDTO.builder().id(id).pw(pw).build());
    }

}