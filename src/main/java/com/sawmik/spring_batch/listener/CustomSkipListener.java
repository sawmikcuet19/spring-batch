package com.sawmik.spring_batch.listener;

import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomSkipListener implements SkipListener<Object, Object> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("SKIPPED READ: {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        log.warn("SKIPPED WRITE: {} | Reason: {}", item, t.getMessage());
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        log.warn("SKIPPED PROCESS: {} | Reason: {}", item, t.getMessage());
    }
}
