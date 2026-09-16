package com.sawmik.spring_batch.listener;

import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.annotation.OnWriteError;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Component
public class ItemWriteAuditListener {

    @BeforeWrite
    public void beforeWrite(List<? extends Object> items) {
        log.trace("Writing {} items...", items.size());
    }

    @AfterWrite
    public void afterWrite(List<? extends Object> items) {
        log.trace("Successfully wrote {} items", items.size());
    }

    @OnWriteError
    public void onWriteError(Exception exception, List<? extends Object> items) {
        log.error("Error writing {} items: {}", items.size(), exception.getMessage());
    }
}
