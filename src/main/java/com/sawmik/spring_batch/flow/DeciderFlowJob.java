package com.sawmik.spring_batch.flow;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import java.time.LocalDateTime;

@Configuration
public class DeciderFlowJob {

    @Bean
    public JobExecutionDecider timeOfDayDecider(JobRepository jobRepository) {
        return new JobExecutionDecider() {
            @Override
            public FlowExecutionStatus decide(
                    org.springframework.batch.core.job.JobExecution jobExecution,
                    org.springframework.batch.core.step.StepExecution stepExecution) {
                int hour = LocalDateTime.now().getHour();
                String exitCode;
                if (hour < 12) {
                    exitCode = "COMPLETED";
                } else if (hour < 18) {
                    exitCode = "STOPPED";
                } else {
                    exitCode = "FAILED";
                }
                return new FlowExecutionStatus(exitCode);
            }
        };
    }

    @Bean
    public Job deciderJobBean(JobRepository jobRepository, JobExecutionDecider timeOfDayDecider,
                           Step morningStep, Step afternoonStep, Step eveningStep) {
        return new JobBuilder("deciderJob", jobRepository)
                .start(timeOfDayDecider)
                    .on("COMPLETED").to(morningStep)
                .from(timeOfDayDecider)
                    .on("STOPPED").to(afternoonStep)
                .from(timeOfDayDecider)
                    .on("FAILED").to(eveningStep)
                .from(morningStep).on("*").end()
                .from(afternoonStep).on("*").end()
                .from(eveningStep).on("*").end()
                .build().build();
    }

    @Bean
    public Step morningStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("morningStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).info("Executing MORNING batch processing");
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step afternoonStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("afternoonStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).info("Executing AFTERNOON batch processing");
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step eveningStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("eveningStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).info("Executing EVENING batch processing");
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
