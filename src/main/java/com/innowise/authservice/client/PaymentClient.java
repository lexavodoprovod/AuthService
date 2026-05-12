package com.innowise.authservice.client;

import com.innowise.authservice.client.fallback.PaymentClientFallBackFactory;
import com.innowise.authservice.dto.request.PaymentRequestDto;
import com.innowise.authservice.dto.response.PaymentResponseDto;
import com.innowise.authservice.entity.PaymentStatus;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.innowise.authservice.constant.PaginationSettings.*;


@FeignClient(name = "payment-service",
        path = "/api/v1/payments",
        fallbackFactory = PaymentClientFallBackFactory.class)
public interface PaymentClient {

    @PostMapping
    PaymentResponseDto addPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto);

    @GetMapping("/{id}")
    PaymentResponseDto getPayment(@PathVariable String id);

    @GetMapping
    Page<PaymentResponseDto> getPayments(
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable
    );

    @GetMapping("/search")
    Page<PaymentResponseDto> getPaymentsByUserIdOrOrderIdOrStatus(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable
    );

    @GetMapping("/sum")
    Long getSumByDateRange(
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            @RequestParam(required = false) Long userId
    );

    @PatchMapping("/{id}")
    PaymentResponseDto changePaymentStatus(
            @PathVariable String id,
            @RequestBody PaymentStatus status
    );

    @DeleteMapping("/{id}")
    Void deletePayment(@PathVariable String id);
}
