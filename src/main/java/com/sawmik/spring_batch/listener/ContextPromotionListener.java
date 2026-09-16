package com.sawmik.spring_batch.listener;

import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ContextPromotionListener {

    @BeforeRead
    public void beforeRead() {
        log.trace("Before read - promoting context");
    }

    @AfterWrite
    public void afterWrite(java.util.List<? extends Object> items) {
        log.trace("After write - promoted {} items to context", items.size());
    }
}
