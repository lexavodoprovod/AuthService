package com.innowise.authservice.exception;

public class OrderServiceException extends FeignServiceException {

    public OrderServiceException() {
        super("OrderService is unavailable");
    }
}
