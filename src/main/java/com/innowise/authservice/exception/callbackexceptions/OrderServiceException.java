package com.innowise.authservice.exception.callbackexceptions;

import com.innowise.authservice.exception.InternalServiceException;

public class OrderServiceException extends InternalServiceException {

    public OrderServiceException() {
        super("OrderService is unavailable (Callback)");
    }
}
