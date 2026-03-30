package com.innowise.authservice.exception;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String username) {
        super("Could not find user with username[%s]".formatted(username));
    }
}
