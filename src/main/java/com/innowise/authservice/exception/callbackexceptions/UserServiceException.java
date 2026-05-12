package com.innowise.authservice.exception.callbackexceptions;

import com.innowise.authservice.exception.InternalServiceException;

public class UserServiceException extends InternalServiceException {
    public UserServiceException() {
        super("UserService is unavailable (Callback)");
    }
}
