package com.innowise.authservice.exception.callbackexceptions;

import com.innowise.authservice.exception.InternalServiceException;

public class PaymentServiceException extends InternalServiceException {
    public PaymentServiceException() {
        super("PaymentService is unavailable (Callback)");
    }
}
