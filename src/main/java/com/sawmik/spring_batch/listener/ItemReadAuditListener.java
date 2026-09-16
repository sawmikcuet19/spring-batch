package com.sawmik.spring_batch.listener;

import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemReadAuditListener {

    @BeforeRead
    public void beforeRead() {
        log.trace("Reading next item...");
    }

    @AfterRead
    public void afterRead(Object item) {
        if (item != null) {
            log.trace("Read item: {}", item.getClass().getSimpleName());
        }
    }

    @OnReadError
    public void onReadError(Exception ex) {
        log.error("Error reading item: {}", ex.getMessage());
    }
}
