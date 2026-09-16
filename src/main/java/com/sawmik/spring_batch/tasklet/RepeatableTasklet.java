package com.sawmik.spring_batch.tasklet;

import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RepeatableTasklet implements Tasklet {

    private int executionCount = 0;
    private static final int MAX_EXECUTIONS = 5;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        executionCount++;
        log.info("RepeatableTasklet execution #{}/{}", executionCount, MAX_EXECUTIONS);

        contribution.getStepExecution().getExecutionContext()
                .putInt("executionCount", executionCount);

        if (executionCount >= MAX_EXECUTIONS) {
            executionCount = 0;
            return RepeatStatus.FINISHED;
        }
        return RepeatStatus.CONTINUABLE;
    }
}
