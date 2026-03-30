package com.innowise.authservice.client;

import com.innowise.authservice.client.fallback.PaymentClientFallBack;
import com.innowise.authservice.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-client",
        url = "${PAYMENT_SERVICE_URL}",
        path = "/payments",
        fallback = PaymentClientFallBack.class)
public interface PaymentClient {

    @PostMapping
    PaymentDto addPayment(@RequestBody PaymentDto paymentDto);

    @GetMapping
    Page<PaymentDto> getAllPayments();

    @PutMapping("/{id}")
    PaymentDto updatePayment(@PathVariable Long id, @RequestBody PaymentDto paymentDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePayment(@PathVariable Long id);
}
