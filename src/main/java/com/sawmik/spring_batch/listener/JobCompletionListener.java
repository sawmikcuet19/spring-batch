package com.sawmik.spring_batch.listener;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobCompletionListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("=== JOB STARTED: {} | Parameters: {} ===",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobParameters());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        log.info("=== JOB FINISHED: {} | Status: {} ===",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus());
    }
}
