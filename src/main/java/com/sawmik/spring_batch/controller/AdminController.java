package com.sawmik.spring_batch.controller;

import com.sawmik.spring_batch.service.BatchAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/batch/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BatchAdminService batchAdminService;
    private final List<Job> allJobs;

    @GetMapping("/jobs")
    public ResponseEntity<Map<String, Object>> listJobs() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("registeredJobs", allJobs.stream().map(Job::getName).toList());
        response.put("jobCount", allJobs.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/jobs/{executionId}")
    public ResponseEntity<Map<String, Object>> getJobExecution(@PathVariable Long executionId) {
        Map<String, Object> status = batchAdminService.getJobStatus(executionId);
        if (status.containsKey("error")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    @PostMapping("/stop/{executionId}")
    public ResponseEntity<Map<String, Object>> stopJob(@PathVariable Long executionId) {
        try {
            batchAdminService.stopJob(executionId);
            return ResponseEntity.ok(Map.of(
                    "message", "Stop signal sent",
                    "executionId", executionId
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "registeredJobs", allJobs.size(),
                "jobNames", allJobs.stream().map(Job::getName).toList(),
                "totalExecutions", batchAdminService.getAllExecutions().size()
        ));
    }
}
