package com.highlightservice.highlightservice;

public class HighlightServiceException extends RuntimeException {

    public HighlightServiceException(String message) {
        super(message);
    }

    public HighlightServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}