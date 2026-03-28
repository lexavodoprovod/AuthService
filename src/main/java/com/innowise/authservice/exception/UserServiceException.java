package com.innowise.authservice.exception;

public class UserServiceException extends FeignServiceException {
    public UserServiceException() {
        super("UserService is unavailable");
    }
}
