package com.sawmik.spring_batch.controller;

import com.sawmik.spring_batch.service.BatchAdminService;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchJobController {

    private final JobOperator jobOperator;
    private final BatchAdminService batchAdminService;
    private final List<Job> allJobs;

    private final AtomicInteger requestCounter = new AtomicInteger(0);
    private final Map<String, List<Map<String, Object>>> jobHistory = new ConcurrentHashMap<>();

    @PostMapping("/launch")
    public ResponseEntity<Map<String, Object>> launchJob(@RequestBody JobLaunchRequest request) {
        try {
            Job job = findJob(request.getJobName());
            if (job == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Job not found: " + request.getJobName(),
                        "availableJobs", allJobs.stream().map(Job::getName).toList()
                ));
            }

            JobParametersBuilder builder = new JobParametersBuilder();
            builder.addLong("requestId", System.currentTimeMillis());
            builder.addLong("timestamp", System.currentTimeMillis());
            builder.addString("inputDir", request.getInputDir() != null ? request.getInputDir() : "Csv_Files");
            builder.addString("outputDir", request.getOutputDir() != null ? request.getOutputDir() : "output");

            if (request.getParams() != null) {
                request.getParams().forEach(builder::addString);
            }

            JobExecution execution = jobOperator.run(job, builder.toJobParameters());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Job launched successfully");
            response.put("jobName", request.getJobName());
            response.put("executionId", execution.getId());
            response.put("status", execution.getStatus().toString());
            response.put("requestNumber", requestCounter.incrementAndGet());
            response.put("launchTime", execution.getStartTime());

            jobHistory.computeIfAbsent(request.getJobName(), k -> new CopyOnWriteArrayList<>()).add(response);

            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", e.getMessage(),
                    "jobName", request.getJobName()
            ));
        }
    }

    @PostMapping("/launch-async")
    public ResponseEntity<Map<String, Object>> launchJobAsync(@RequestBody JobLaunchRequest request) {
        try {
            Job job = findJob(request.getJobName());
            if (job == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Job not found: " + request.getJobName()));
            }

            CompletableFuture.supplyAsync(() -> {
                try {
                    JobParametersBuilder builder = new JobParametersBuilder();
                    builder.addLong("requestId", System.currentTimeMillis());
                    builder.addLong("timestamp", System.currentTimeMillis());
                    builder.addString("inputDir", request.getInputDir() != null ? request.getInputDir() : "Csv_Files");
                    builder.addString("outputDir", request.getOutputDir() != null ? request.getOutputDir() : "output");
                    if (request.getParams() != null) {
                        request.getParams().forEach(builder::addString);
                    }
                    return jobOperator.run(job, builder.toJobParameters());
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            });

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Job launch initiated (async)");
            response.put("jobName", request.getJobName());
            response.put("requestNumber", requestCounter.incrementAndGet());
            response.put("status", "LAUNCHING");

            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/launch-all")
    public ResponseEntity<Map<String, Object>> launchAllJobs() {
        Map<String, Object> results = new LinkedHashMap<>();
        int launched = 0;

        for (Job job : allJobs) {
            try {
                JobParametersBuilder builder = new JobParametersBuilder();
                builder.addLong("timestamp", System.currentTimeMillis());
                builder.addString("trigger", "launch-all");

                JobExecution execution = jobOperator.run(job, builder.toJobParameters());
                results.put(job.getName(), Map.of(
                        "executionId", execution.getId(),
                        "status", execution.getStatus().toString()
                ));
                launched++;
            } catch (Exception e) {
                results.put(job.getName(), Map.of("error", e.getMessage()));
            }
        }

        results.put("totalLaunched", launched);
        results.put("totalRegistered", allJobs.size());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/concurrent-test")
    public ResponseEntity<Map<String, Object>> concurrentTest(@RequestParam(defaultValue = "10") int threadCount) {
        List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            Job job = allJobs.get(i % allJobs.size());
            int threadNum = i + 1;

            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    JobParametersBuilder builder = new JobParametersBuilder();
                    builder.addLong("timestamp", System.currentTimeMillis());
                    builder.addLong("threadId", (long) threadNum);
                    builder.addString("trigger", "concurrent-test");

                    long start = System.currentTimeMillis();
                    JobExecution execution = jobOperator.run(job, builder.toJobParameters());
                    long duration = System.currentTimeMillis() - start;

                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("thread", Thread.currentThread().getName());
                    result.put("jobName", job.getName());
                    result.put("executionId", execution.getId());
                    result.put("status", execution.getStatus().toString());
                    result.put("launchDurationMs", duration);
                    return result;
                } catch (Exception e) {
                    Map<String, Object> errorResult = new LinkedHashMap<>();
                    errorResult.put("thread", Thread.currentThread().getName());
                    errorResult.put("error", e.getMessage());
                    return errorResult;
                }
            }, executor));
        }

        List<Map<String, Object>> results = futures.stream()
                .map(f -> {
                    try { return f.get(60, TimeUnit.SECONDS); }
                    catch (Exception e) {
                        Map<String, Object> errorResult = new LinkedHashMap<>();
                        errorResult.put("error", e.getMessage());
                        return errorResult;
                    }
                })
                .toList();

        executor.shutdown();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalRequests", threadCount);
        response.put("results", results);
        response.put("summary", Map.of(
                "successful", results.stream().filter(r -> "COMPLETED".equals(r.get("status"))).count(),
                "failed", results.stream().filter(r -> r.containsKey("error")).count()
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{executionId}")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable Long executionId) {
        return ResponseEntity.ok(batchAdminService.getJobStatus(executionId));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<String>> getRegisteredJobs() {
        return ResponseEntity.ok(allJobs.stream().map(Job::getName).toList());
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getJobHistory() {
        return ResponseEntity.ok(jobHistory);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRequestsProcessed", requestCounter.get());
        stats.put("registeredJobs", allJobs.size());
        stats.put("jobNames", allJobs.stream().map(Job::getName).toList());
        stats.put("jobHistoryCount", jobHistory.values().stream().mapToInt(List::size).sum());
        return ResponseEntity.ok(stats);
    }

    private Job findJob(String name) {
        return allJobs.stream().filter(j -> j.getName().equals(name)).findFirst().orElse(null);
    }

    @Data
    public static class JobLaunchRequest {
        private String jobName;
        private String inputDir;
        private String outputDir;
        private Map<String, String> params;
    }
}
