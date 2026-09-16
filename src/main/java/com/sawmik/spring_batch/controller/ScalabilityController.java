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
@RequestMapping("/api/batch/scalability")
@RequiredArgsConstructor
public class ScalabilityController {

    private final JobOperator jobOperator;
    private final List<Job> allJobs;

    private final AtomicInteger jobCounter = new AtomicInteger(0);

    @PostMapping("/multi-thread")
    public ResponseEntity<Map<String, Object>> launchMultiThreadedJob() {
        return launchJobByName("multiThreadedJob");
    }

    @PostMapping("/partition")
    public ResponseEntity<Map<String, Object>> launchPartitionedJob() {
        return launchJobByName("partitionedJob");
    }

    @PostMapping("/file-partition")
    public ResponseEntity<Map<String, Object>> launchFilePartitionJob() {
        return launchJobByName("filePartitionJob");
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<String>> getScalabilityJobs() {
        List<String> scalableJobs = allJobs.stream()
                .map(Job::getName)
                .filter(n -> n.contains("multiThread") || n.contains("partition") || n.contains("split"))
                .toList();
        return ResponseEntity.ok(scalableJobs);
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
            builder.addString("trigger", "scalability-test");

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
