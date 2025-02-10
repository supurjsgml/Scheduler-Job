//package com.app.job.jobKorea;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import com.app.job.jobKorea.service.JobKoreaResumeUpdaterService;
//
//
//
//@Configuration
//@EnableBatchProcessing
//public class JobKoreaJob {
//
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//    private final JobKoreaResumeUpdaterService updater;
//
//    public JobKoreaJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobKoreaResumeUpdaterService updater) {
//        this.jobRepository = jobRepository;
//        this.transactionManager = transactionManager;
//        this.updater = updater;
//    }
//
//    @Bean
//    public Step updateResumeStep() {
//        return new StepBuilder("updateResumeStep", jobRepository)
//                .tasklet((contribution, chunkContext) -> {
//                    updater.updateResume();
//                    return RepeatStatus.FINISHED;
//                }, transactionManager)
//                .build();
//    }
//
//    @Bean
//    public Job updateResumeJob() {
//        return new JobBuilder("updateResumeJob", jobRepository)
//                .start(updateResumeStep())
//                .build();
//    }
//}
