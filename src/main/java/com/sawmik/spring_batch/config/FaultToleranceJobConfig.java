package com.sawmik.spring_batch.config;

import com.sawmik.spring_batch.exception.SkippableException;
import com.sawmik.spring_batch.exception.RetryableException;
import com.sawmik.spring_batch.listener.CustomSkipListener;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class FaultToleranceJobConfig {

    @Bean
    public Job skipJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        AtomicInteger counter = new AtomicInteger(0);
        return new JobBuilder("skipJob", jobRepository)
                .start(new StepBuilder("skipStep", jobRepository)
                        .<String, String>chunk(10, transactionManager)
                        .reader(() -> {
                            int i = counter.incrementAndGet();
                            if (i > 100) return null;
                            if (i % 20 == 0) {
                                throw new SkippableException("Bad record at position " + i);
                            }
                            return "Item-" + i;
                        })
                        .processor((ItemProcessor<String, String>) item -> {
                            if (item.contains("50")) {
                                throw new SkippableException("Cannot process: " + item);
                            }
                            return item.toUpperCase();
                        })
                        .writer(items -> {
                            org.slf4j.LoggerFactory.getLogger(getClass()).info("Writing {} items", items.size());
                        })
                        .faultTolerant()
                        .skipLimit(50)
                        .skip(SkippableException.class)
                        .listener(new CustomSkipListener())
                        .build())
                .build();
    }

    @Bean
    public Job retryJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        AtomicInteger failCount = new AtomicInteger(0);
        return new JobBuilder("retryJob", jobRepository)
                .start(new StepBuilder("retryStep", jobRepository)
                        .<String, String>chunk(10, transactionManager)
                        .reader(() -> {
                            int i = failCount.incrementAndGet();
                            if (i > 100) return null;
                            return "Item-" + i;
                        })
                        .processor((ItemProcessor<String, String>) item -> {
                            if (item.contains("15")) {
                                throw new RetryableException("Transient error for: " + item);
                            }
                            return item.toUpperCase();
                        })
                        .writer(items -> {
                            org.slf4j.LoggerFactory.getLogger(getClass()).info("Writing {} items", items.size());
                        })
                        .faultTolerant()
                        .retryLimit(3)
                        .retry(RetryableException.class)
                        .build())
                .build();
    }

    @Bean
    public Job skipRetryJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        AtomicInteger counter = new AtomicInteger(0);
        return new JobBuilder("skipRetryJob", jobRepository)
                .start(new StepBuilder("skipRetryStep", jobRepository)
                        .<String, String>chunk(10, transactionManager)
                        .reader(() -> {
                            int i = counter.incrementAndGet();
                            if (i > 100) return null;
                            if (i % 30 == 0) throw new SkippableException("Skip at " + i);
                            return "Item-" + i;
                        })
                        .processor((ItemProcessor<String, String>) item -> {
                            if (item.contains("25")) {
                                throw new RetryableException("Retry for: " + item);
                            }
                            return item.toUpperCase();
                        })
                        .writer(items -> {
                            org.slf4j.LoggerFactory.getLogger(getClass()).info("Writing {} items", items.size());
                        })
                        .faultTolerant()
                        .skipLimit(20)
                        .skip(SkippableException.class)
                        .retryLimit(3)
                        .retry(RetryableException.class)
                        .listener(new CustomSkipListener())
                        .build())
                .build();
    }
}
