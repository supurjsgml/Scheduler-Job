package com.app.job.quartz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.app.common.utils.DateUtils;
import com.app.job.quartz.QuartzJob;
import com.app.job.quartz.dto.res.QuartzLiveJobsResponseDto;
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

	public static final String GROUP_NAME = ":group";
	
	private static final String QUARTZ_GROUP_NAME = "quartz:group";

    private final Scheduler scheduler;
    
    private final MeterRegistry registry;

    @PostConstruct
    public void initJobs() throws SchedulerException {
    	scheduler.getListenerManager().addJobListener(new JobExecutionMetricsListener(registry));
    	
        // 이력서 갱신 Job
        registerJob("QuartzJob", QUARTZ_GROUP_NAME, QuartzJob.class, "jobKoreaResumeUpdate", "0 0/10 * * * ?"); // 10분마다
        // KeepAlive Job
        registerJob("StilALiveJob", QUARTZ_GROUP_NAME, StilALiveJob.class, "StilALiveJob", "0 0/15 * * * ?");   // 15분마다
    }

    public void registerJob(String jobName, String group, Class<? extends Job> jobClass, String triggerName, String cronExpression) throws SchedulerException {
    	registerJob(jobName, group, jobClass, triggerName, cronExpression, null);
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
            TriggerKey triggerKey = new TriggerKey(triggerName, StringUtils.isBlank(groupName) ? QUARTZ_GROUP_NAME : groupName);
            scheduler.pauseTrigger(triggerKey);
            log.info("트리거가 일시 중지되었습니다: {}", triggerName);
        } catch (SchedulerException e) {
            log.error("트리거 일시 중지 실패: {}", e.getMessage());
            throw new RuntimeException("트리거 일시 중지 중 오류 발생", e);
        }
    }

    // 트리거 재개
    public void resumeJob(String triggerName, String groupName) {
        try {
            TriggerKey triggerKey = new TriggerKey(triggerName, StringUtils.isBlank(groupName) ? QUARTZ_GROUP_NAME : groupName);
            scheduler.resumeTrigger(triggerKey);
            log.info("트리거가 재개되었습니다: {}", triggerName);
        } catch (SchedulerException e) {
            log.error("트리거 재개 실패: {}", e.getMessage());
            throw new RuntimeException("트리거 재개 중 오류 발생", e);
        }
    }

    // 트리거 스케줄 변경
    public void rescheduleJob(String triggerName, String groupName, String jobName, String newCronExpression) {
        try {
        	groupName = StringUtils.isBlank(groupName) ? QUARTZ_GROUP_NAME : groupName;
        	
            CronScheduleBuilder.cronSchedule(newCronExpression); // 크론 유효성 체크
            TriggerKey triggerKey = new TriggerKey(triggerName, groupName);
            CronTrigger newTrigger = TriggerBuilder.newTrigger()
                    .forJob(jobName, groupName)
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
    
    /**
     * 현재 존재하는 job 조회
     * @return List<QuartzLiveJobsResponseDto>
     * @throws SchedulerException
     * @author guney
     * @date 2026. 3. 9.
     */
    public List<QuartzLiveJobsResponseDto> getAllJobs() {
        List<QuartzLiveJobsResponseDto> jobs = new ArrayList<>();

        try {
        	// 모든 JobGroup을 순회
        	for (String groupName : scheduler.getJobGroupNames()) {
        		// 그룹 내 모든 JobKey 조회
        		for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
        			
        			String jobName = jobKey.getName();
        			String group = jobKey.getGroup();
        			
        			// 해당 잡에 연결된 트리거들 조회
        			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        			
        			for (Trigger trigger : triggers) {
        				Trigger.TriggerState state = scheduler.getTriggerState(trigger.getKey());
        				
        				jobs.add(QuartzLiveJobsResponseDto.builder()
        						.jobName(jobName)
        						.groupName(group)
        						.nextFireTime(DateUtils.localDateTimeToString(DateUtils.toLocalDateTime(trigger.getNextFireTime())))
        						.status(state.name())
        						.build());
        			}
        		}
        	}
			
		} catch (SchedulerException e) {
			log.error("QuartzService getAllJobs ERROR : {}", e);
		}
        
        return jobs;
    }
    
    /**
     * 사용자 job 조회
     * @param userId
     * @return String
     * @throws SchedulerException
     * @author guney
     * @date 2026. 3. 10.
     */
    public QuartzLiveJobsResponseDto getUserJobStatus(String userId) {
        
    	String nextFireTime = null;
    	Trigger trigger = null;
    	Trigger.TriggerState state = TriggerState.NONE;
    	
    	JobKey jobKey = JobKey.jobKey(userId, userId.concat(GROUP_NAME));
    	
    	try {
			scheduler.getTriggersOfJob(jobKey);
			
	    	List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
	    	
	    	if (!CollectionUtils.isEmpty(triggers)) {
	    		trigger = triggers.getFirst();
	    		state = scheduler.getTriggerState(trigger.getKey());
	    		nextFireTime = DateUtils.localDateTimeToString(DateUtils.toLocalDateTime(trigger.getNextFireTime()));
			}
	    			
		} catch (SchedulerException e) {
			log.error("QuartzService getUserJobStatus ERROR : {}", e);
		}
    		
        return QuartzLiveJobsResponseDto.builder()
        		.jobName(jobKey.getName())
				.groupName(jobKey.getGroup())
				.nextFireTime(nextFireTime)
				.status(state.name())
        		.build();
    }
    
}