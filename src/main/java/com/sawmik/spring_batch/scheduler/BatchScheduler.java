package com.sawmik.spring_batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final AtomicInteger scheduledRunCount = new AtomicInteger(0);

    @Scheduled(cron = "0 */5 * * * *")
    public void scheduledDataProcessing() {
        log.info("=== SCHEDULED JOB TRIGGERED (Run #{}) ===", scheduledRunCount.incrementAndGet());
        log.info("This would launch a batch job in production");
    }

    @Scheduled(fixedRate = 60000)
    public void healthCheck() {
        log.debug("Batch scheduler health check - Run count: {}", scheduledRunCount.get());
    }
}
