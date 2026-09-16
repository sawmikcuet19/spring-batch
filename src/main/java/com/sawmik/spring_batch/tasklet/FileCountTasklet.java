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
public class FileCountTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        String inputDir = (String) chunkContext.getStepContext()
                .getJobParameters().get("inputDir");
        if (inputDir == null) {
            inputDir = "Csv_Files";
        }

        File dir = new File(inputDir);
        int csvCount = 0;
        long totalSize = 0;

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".csv")) {
                        csvCount++;
                        totalSize += file.length();
                    }
                }
            }
        }

        chunkContext.getStepContext().getStepExecution()
                .getExecutionContext().putInt("csvFileCount", csvCount);
        chunkContext.getStepContext().getStepExecution()
                .getExecutionContext().putLong("totalSizeBytes", totalSize);

        log.info("Found {} CSV files totaling {} bytes in {}", csvCount, totalSize, inputDir);
        return RepeatStatus.FINISHED;
    }
}
