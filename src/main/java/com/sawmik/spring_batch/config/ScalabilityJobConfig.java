package com.sawmik.spring_batch.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ScalabilityJobConfig {

    @Bean
    public TaskExecutor multiThreadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("mt-chunk-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Job multiThreadedJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        AtomicInteger counter = new AtomicInteger(0);
        return new JobBuilder("multiThreadedJob", jobRepository)
                .start(new StepBuilder("multiThreadStep", jobRepository)
                        .<String, String>chunk(100, transactionManager)
                        .reader(() -> {
                            int i = counter.incrementAndGet();
                            if (i > 10000) return null;
                            return "MT-Item-" + i;
                        })
                        .processor(item -> item.toUpperCase())
                        .writer(items -> {
                            org.slf4j.LoggerFactory.getLogger(getClass()).info("[Thread: {}] Writing {} items",
                                    Thread.currentThread().getName(), items.size());
                        })
                        .taskExecutor(multiThreadExecutor())
                        .build())
                .build();
    }

    @Bean
    public Job partitionedJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("partitionedJob", jobRepository)
                .start(new StepBuilder("masterStep", jobRepository)
                        .partitioner("workerStep", new org.springframework.batch.core.partition.support.SimplePartitioner())
                        .step(new StepBuilder("workerStep", jobRepository)
                                .<String, String>chunk(100, transactionManager)
                                .reader(() -> {
                                    int i = new java.util.Random().nextInt(10000) + 1;
                                    return "Partition-Item-" + i;
                                })
                                .writer(items -> {
                                    org.slf4j.LoggerFactory.getLogger(getClass()).info("[Partition] Writing {} items", items.size());
                                })
                                .build())
                        .partitionHandler(new org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler() {{
                            setTaskExecutor(multiThreadExecutor());
                            setGridSize(5);
                        }})
                        .build())
                .build();
    }

    @Bean
    public Job filePartitionJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("filePartitionJob", jobRepository)
                .start(new StepBuilder("filePartitionMaster", jobRepository)
                        .partitioner("fileWorkerStep", new org.springframework.batch.core.partition.support.SimplePartitioner())
                        .step(new StepBuilder("fileWorkerStep", jobRepository)
                                .tasklet((contribution, chunkContext) -> {
                                    org.slf4j.LoggerFactory.getLogger(getClass()).info(
                                            "[FilePartition] Processing on thread: {}", Thread.currentThread().getName());
                                    Thread.sleep(500);
                                    return RepeatStatus.FINISHED;
                                }, transactionManager)
                                .build())
                        .partitionHandler(new org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler() {{
                            setTaskExecutor(multiThreadExecutor());
                            setGridSize(4);
                        }})
                        .build())
                .build();
    }
}
