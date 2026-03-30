package com.innowise.authservice.exception;

import org.springframework.http.HttpStatus;

public class NullParameterException extends BusinessException {
    public NullParameterException() {
        super("Try to use null parameter in UserServiceImpl", HttpStatus.BAD_REQUEST);
    }
}
