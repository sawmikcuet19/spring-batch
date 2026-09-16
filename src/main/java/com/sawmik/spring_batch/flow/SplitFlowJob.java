package com.sawmik.spring_batch.flow;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SplitFlowJob {

    @Bean
    public Job splitFlowJobBean(JobRepository jobRepository, Step parallelStep1, Step parallelStep2, Step parallelStep3,
                            AsyncTaskExecutor splitFlowExecutor) {
        Flow flow2and3 = new FlowBuilder<Flow>("parallelFlow2and3")
                .start(parallelStep2).next(parallelStep3)
                .build();

        return new JobBuilder("splitFlowJob", jobRepository)
                .start(parallelStep1)
                .split(splitFlowExecutor)
                    .add(flow2and3)
                .end()
                .build();
    }

    @Bean
    public AsyncTaskExecutor splitFlowExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("split-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Step parallelStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("parallelStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).info("Parallel Step 1 - Thread: {}", Thread.currentThread().getName());
                    Thread.sleep(1000);
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step parallelStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("parallelStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).info("Parallel Step 2 - Thread: {}", Thread.currentThread().getName());
                    Thread.sleep(1000);
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step parallelStep3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("parallelStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).info("Parallel Step 3 - Thread: {}", Thread.currentThread().getName());
                    Thread.sleep(1000);
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
