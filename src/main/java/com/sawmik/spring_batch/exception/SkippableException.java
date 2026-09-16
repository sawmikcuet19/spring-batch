package com.sawmik.spring_batch.exception;

public class SkippableException extends RuntimeException {
    public SkippableException(String message) {
        super(message);
    }

    public SkippableException(String message, Throwable cause) {
        super(message, cause);
    }
}
