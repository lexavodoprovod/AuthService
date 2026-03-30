package com.innowise.authservice.exception;

import org.springframework.http.HttpStatus;

public class ExistUserException extends BusinessException {
    public ExistUserException(String username) {
        super("User with username [%s] already exists!".formatted(username), HttpStatus.CONFLICT);
    }
}
