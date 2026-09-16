package com.sawmik.spring_batch.flow;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ChainedJobConfig {

    @Bean
    public Job chainedJobFirstBean(JobRepository jobRepository, Step firstJobStep1) {
        return new JobBuilder("chainedJobFirst", jobRepository)
                .start(firstJobStep1)
                .build();
    }

    @Bean
    public Job chainedJobSecondBean(JobRepository jobRepository, Step secondJobStep1) {
        return new JobBuilder("chainedJobSecond", jobRepository)
                .start(secondJobStep1)
                .build();
    }

    @Bean
    public Step firstJobStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("firstJobStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).info("First Job - Step 1 completed");
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().putString("firstJobResult", "SUCCESS");
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step secondJobStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("secondJobStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String firstJobResult = (String) chunkContext.getStepContext()
                            .getStepExecution().getJobExecution().getExecutionContext().get("firstJobResult");
                    org.slf4j.LoggerFactory.getLogger(getClass()).info("Second Job - Step 1 completed | First job result: {}", firstJobResult);
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
