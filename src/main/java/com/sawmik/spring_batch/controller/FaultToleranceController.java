package com.sawmik.spring_batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/batch/fault-tolerance")
@RequiredArgsConstructor
public class FaultToleranceController {

    private final JobOperator jobOperator;
    private final List<Job> allJobs;

    private final AtomicInteger jobCounter = new AtomicInteger(0);

    @PostMapping("/skip")
    public ResponseEntity<Map<String, Object>> launchSkipJob() {
        return launchJobByName("skipJob");
    }

    @PostMapping("/retry")
    public ResponseEntity<Map<String, Object>> launchRetryJob() {
        return launchJobByName("retryJob");
    }

    @PostMapping("/skip-retry")
    public ResponseEntity<Map<String, Object>> launchSkipRetryJob() {
        return launchJobByName("skipRetryJob");
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<String>> getFaultToleranceJobs() {
        List<String> ftJobs = allJobs.stream()
                .map(Job::getName)
                .filter(n -> n.contains("skip") || n.contains("retry"))
                .toList();
        return ResponseEntity.ok(ftJobs);
    }

    private ResponseEntity<Map<String, Object>> launchJobByName(String jobName) {
        try {
            Job job = allJobs.stream().filter(j -> j.getName().equals(jobName)).findFirst().orElse(null);
            if (job == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Job not found: " + jobName));
            }

            JobParametersBuilder builder = new JobParametersBuilder();
            builder.addLong("timestamp", System.currentTimeMillis());
            builder.addLong("jobId", (long) jobCounter.incrementAndGet());
            builder.addString("trigger", "fault-tolerance-test");

            JobExecution execution = jobOperator.run(job, builder.toJobParameters());
            return ResponseEntity.accepted().body(Map.of(
                    "message", jobName + " launched",
                    "executionId", execution.getId(),
                    "status", execution.getStatus().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
