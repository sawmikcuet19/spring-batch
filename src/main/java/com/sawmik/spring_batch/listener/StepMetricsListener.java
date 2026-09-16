package com.sawmik.spring_batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StepMetricsListener {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info(">>> STEP STARTED: {}", stepExecution.getStepName());
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("<<< STEP COMPLETED: {} | Read: {} | Written: {} | Committed: {} | Skipped: {}",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getCommitCount(),
                stepExecution.getSkipCount());
        return stepExecution.getExitStatus();
    }
}
