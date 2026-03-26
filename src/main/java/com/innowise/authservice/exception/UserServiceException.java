package com.innowise.authservice.exception;

public class UserServiceException extends RuntimeException {
    public UserServiceException() {
        super("User Service is unavailable");
    }
}
