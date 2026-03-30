package com.innowise.authservice.client.fallback;

import com.innowise.authservice.client.PaymentClient;
import com.innowise.authservice.dto.PaymentDto;
import com.innowise.authservice.exception.PaymentServiceException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentClientFallBack implements PaymentClient {
    @Override
    public PaymentDto addPayment(PaymentDto paymentDto) {
        throw  new PaymentServiceException();
    }

    @Override
    public Page<PaymentDto> getAllPayments() {
        throw new PaymentServiceException();
    }

    @Override
    public PaymentDto updatePayment(Long id, PaymentDto paymentDto) {
        throw new PaymentServiceException();
    }

    @Override
    public ResponseEntity<Void> deletePayment(Long id) {
        throw new PaymentServiceException();
    }
}
