package com.sawmik.spring_batch.listener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChunkTimingListener {

    private long chunkStart;

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        chunkStart = System.currentTimeMillis();
        log.debug("Chunk starting...");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        long duration = System.currentTimeMillis() - chunkStart;
        log.debug("Chunk completed in {}ms", duration);
    }

    @AfterChunkError
    public void afterChunkError(ChunkContext context) {
        log.error("Chunk FAILED at step: {}", context.getStepContext().getStepName());
    }
}
