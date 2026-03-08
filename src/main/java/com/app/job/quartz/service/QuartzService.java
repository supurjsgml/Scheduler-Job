package com.app.job.quartz.service;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import com.app.job.quartz.QuartzJob;
import com.app.job.quartz.quartzListener.JobExecutionMetricsListener;
import com.app.job.stilALive.StilALiveJob;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class QuartzService {

    private static final String GROUP_NAME = "group1";

    private final Scheduler scheduler;
    
    private final MeterRegistry registry;

    @PostConstruct
    public void initJobs() throws SchedulerException {
    	scheduler.getListenerManager().addJobListener(new JobExecutionMetricsListener(registry));
    	
        // 이력서 갱신 Job
        registerJob("QuartzJob", GROUP_NAME, QuartzJob.class, "jobKoreaResumeUpdate", "0 0/10 * * * ?"); // 10분마다
        // KeepAlive Job
        registerJob("StilALiveJob", GROUP_NAME, StilALiveJob.class, "StilALiveJob", "0 0/15 * * * ?");   // 15분마다
    }

    public void registerJob(String jobName, String group, Class<? extends Job> jobClass, String triggerName, String cronExpression) throws SchedulerException {
    	registerJob(cronExpression, cronExpression, jobClass, cronExpression, cronExpression, null);
    }
    
    public void registerJob(String jobName, String group, Class<? extends Job> jobClass, String triggerName, String cronExpression, Map<String, Object> jobDataMap) throws SchedulerException {
    	
    	JobBuilder jobBuilder = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, group);

        //JobDataMap에 데이터가 있다면 SET
        if (ObjectUtils.isNotEmpty(jobDataMap)) {
            jobBuilder.usingJobData(new org.quartz.JobDataMap(jobDataMap));
        }

        JobDetail jobDetail = jobBuilder.storeDurably().build();
    	

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(triggerName, group)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        
        //이미 같은 이름의 잡이 있다면 삭제 후 등록 (중복 방지)
        JobKey jobKey = new JobKey(jobName, group);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Job registered: {} with trigger: {}", jobName, triggerName);
    }

    // 트리거 일시 중지
    public void pauseJob(String triggerName, String groupName) {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, StringUtils.isBlank(groupName) ? GROUP_NAME : groupName);
            scheduler.pauseTrigger(triggerKey);
            log.info("트리거가 일시 중지되었습니다: {}", triggerName);
        } catch (SchedulerException e) {
            log.error("트리거 일시 중지 실패: {}", e.getMessage());
            throw new RuntimeException("트리거 일시 중지 중 오류 발생", e);
        }
    }

    // 트리거 재개
    public void resumeJob(String triggerName) {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, GROUP_NAME);
            scheduler.resumeTrigger(triggerKey);
            log.info("트리거가 재개되었습니다: {}", triggerName);
        } catch (SchedulerException e) {
            log.error("트리거 재개 실패: {}", e.getMessage());
            throw new RuntimeException("트리거 재개 중 오류 발생", e);
        }
    }

    // 트리거 스케줄 변경
    public void rescheduleJob(String triggerName, String jobName, String newCronExpression) {
        try {
            CronScheduleBuilder.cronSchedule(newCronExpression); // 크론 유효성 체크
            TriggerKey triggerKey = new TriggerKey(triggerName, GROUP_NAME);
            CronTrigger newTrigger = TriggerBuilder.newTrigger()
                    .forJob(jobName, GROUP_NAME)
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression))
                    .build();

            scheduler.rescheduleJob(triggerKey, newTrigger);
            log.info("스케줄이 변경되었습니다: {} -> {}", triggerName, newCronExpression);
        } catch (SchedulerException e) {
            log.error("스케줄 변경 실패: {}", e.getMessage());
            throw new RuntimeException("스케줄 변경 중 오류 발생", e);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 크론 표현식: {}", newCronExpression);
            throw new IllegalArgumentException("유효하지 않은 크론 표현식: " + newCronExpression);
        }
    }
}