package com.sawmik.spring_batch.tasklet;

import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.io.File;

@Slf4j
@Component
public class FileCleanupTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        String outputDir = (String) chunkContext.getStepContext()
                .getJobParameters().get("outputDir");
        if (outputDir == null) {
            outputDir = "output";
        }

        File dir = new File(outputDir);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                int deleted = 0;
                for (File file : files) {
                    if (file.getName().endsWith(".csv")) {
                        if (file.delete()) {
                            deleted++;
                        }
                    }
                }
                log.info("Cleanup: Deleted {} CSV files from {}", deleted, outputDir);
            }
        } else {
            log.info("Output directory does not exist: {}", outputDir);
        }
        return RepeatStatus.FINISHED;
    }
}
