package com.sawmik.spring_batch.service;

import org.springframework.batch.core.*;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchAdminService {

    private final JobOperator jobOperator;
    private final JobRepository jobRepository;

    public JobExecution launchJob(String jobName, Job job, Map<String, String> params) throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addLong("timestamp", System.currentTimeMillis());
        params.forEach(builder::addString);

        JobExecution execution = jobOperator.run(job, builder.toJobParameters());
        log.info("Launched job '{}' with execution ID: {}", jobName, execution.getId());
        return execution;
    }

    public void stopJob(Long executionId) throws Exception {
        jobOperator.stop(executionId);
        log.info("Stopped job execution: {}", executionId);
    }

    public Map<String, Object> getJobStatus(Long executionId) {
        try {
            JobExecution execution = jobRepository.getJobExecution(executionId);

            Map<String, Object> status = new LinkedHashMap<>();
            status.put("executionId", execution.getId());
            status.put("jobName", execution.getJobInstance().getJobName());
            status.put("status", execution.getStatus().toString());
            status.put("exitStatus", execution.getExitStatus().getExitCode());
            status.put("startTime", execution.getStartTime());
            status.put("endTime", execution.getEndTime());

            execution.getStepExecutions().forEach(stepExec -> {
                Map<String, Object> stepInfo = new LinkedHashMap<>();
                stepInfo.put("readCount", stepExec.getReadCount());
                stepInfo.put("writeCount", stepExec.getWriteCount());
                stepInfo.put("commitCount", stepExec.getCommitCount());
                stepInfo.put("skipCount", stepExec.getSkipCount());
                stepInfo.put("status", stepExec.getStatus().toString());
                status.put("step:" + stepExec.getStepName(), stepInfo);
            });

            return status;
        } catch (Exception e) {
            return Map.of("error", "Execution not found: " + e.getMessage());
        }
    }

    public Collection<JobExecution> getAllExecutions() {
        List<JobExecution> allExecutions = new ArrayList<>();
        jobRepository.getJobNames().forEach(name -> {
            jobRepository.getJobInstances(name, 0, Integer.MAX_VALUE).forEach(instance -> {
                allExecutions.addAll(jobRepository.getJobExecutions(instance));
            });
        });
        return allExecutions;
    }
}
