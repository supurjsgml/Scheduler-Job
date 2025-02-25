package com.app.job.controller;

import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.job.jobKorea.service.QuartzService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quartz")
@RequiredArgsConstructor
public class QuartzController {

    private final QuartzService quartzService;

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
}