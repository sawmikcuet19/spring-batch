package com.sawmik.spring_batch.scope;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ScopeConfig {

    @Bean
    @StepScope
    public ItemReader<String> stepScopedReader(
            @Value("#{jobParameters['inputSource']}") String inputSource) {
        return new ItemReader<String>() {
            private int index = 0;
            private final List<String> items = Arrays.asList(
                    inputSource + "-Item-1",
                    inputSource + "-Item-2",
                    inputSource + "-Item-3",
                    inputSource + "-Item-4",
                    inputSource + "-Item-5"
            );

            @Override
            public String read() {
                if (index >= items.size()) return null;
                return items.get(index++);
            }
        };
    }

    @Bean
    @JobScope
    public ItemWriter<String> jobScopedWriter() {
        return new ItemWriter<String>() {
            private int totalWritten = 0;

            @Override
            public void write(Chunk<? extends String> chunk) {
                totalWritten += chunk.size();
                org.slf4j.LoggerFactory.getLogger(getClass())
                        .info("[JobScope] Written {} items (total: {})", chunk.size(), totalWritten);
            }
        };
    }

    @Bean
    @StepScope
    public Tasklet dynamicStepTasklet(
            @Value("#{jobExecutionContext['globalConfig']}") String config,
            @Value("#{stepExecutionContext['partitionId']}") String partitionId) {
        return (contribution, chunkContext) -> {
            org.slf4j.LoggerFactory.getLogger(getClass())
                    .info("Step tasklet - Config: {}, Partition: {}", config, partitionId);
            return RepeatStatus.FINISHED;
        };
    }
}
