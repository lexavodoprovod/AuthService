package com.innowise.authservice.exception;

public class PaymentServiceException extends FeignServiceException {
    public PaymentServiceException() {
        super("PaymentService is unavailable");
    }
}
