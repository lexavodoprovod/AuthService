package com.innowise.authservice.exception;

public class FeignServiceException extends RuntimeException {
    public FeignServiceException(String message) {
        super(message);
    }
}
