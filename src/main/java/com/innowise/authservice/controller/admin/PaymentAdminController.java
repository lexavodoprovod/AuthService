package com.innowise.authservice.controller.admin;

import com.innowise.authservice.client.PaymentClient;
import com.innowise.authservice.dto.request.PaymentRequestDto;
import com.innowise.authservice.dto.request.UpdatePaymentStatusRequest;
import com.innowise.authservice.dto.response.PaymentResponseDto;
import com.innowise.authservice.entity.PaymentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.innowise.authservice.constant.PaginationSettings.PAGINATION_SIZE;
import static com.innowise.authservice.constant.PaginationSettings.SORT_BY;

/**
 * Administrative controller for managing financial transactions.
 * Provides endpoints for creating, retrieving, filtering, and deleting payments
 * via communication with the internal payment service.
 */
@RestController
@RequestMapping(value = "/admin/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentAdminController {

    private final PaymentClient paymentClient;

    /**
     * Records a new payment in the system.
     *
     * @param paymentFromAdmin DTO containing payment details to be registered.
     * @return {@link ResponseEntity} containing the created {@link PaymentResponseDto} and HTTP 201 status.
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDto> addPayment(@Valid @RequestBody PaymentRequestDto paymentFromAdmin) {
        PaymentResponseDto paymentDto = paymentClient.addPayment(paymentFromAdmin);
        return new ResponseEntity<>(paymentDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves specific payment details by its unique identifier.
     *
     * @param id the unique identifier of the payment.
     * @return {@link ResponseEntity} containing the {@link PaymentResponseDto}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPayment(@PathVariable String id) {
        PaymentResponseDto paymentDto = paymentClient.getPayment(id);
        return ResponseEntity.ok(paymentDto);
    }

    /**
     * Retrieves a paginated list of all payments.
     *
     * @param pageable pagination and sorting information (default size and sort applied).
     * @return {@link ResponseEntity} containing a {@link Page} of {@link PaymentResponseDto}.
     */
    @GetMapping
    public ResponseEntity<Page<PaymentResponseDto>> getPayments(
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable
    ) {
        Page<PaymentResponseDto> paymentsPage = paymentClient.getPayments(pageable);
        return ResponseEntity.ok(paymentsPage);
    }

    /**
     * Searches for payments based on various filtering criteria such as user ID, order ID, or status.
     *
     * @param userId   optional filter by user ID.
     * @param orderId  optional filter by order ID.
     * @param status   optional filter by current {@link PaymentStatus}.
     * @param pageable pagination and sorting information.
     * @return {@link ResponseEntity} containing a filtered {@link Page} of {@link PaymentResponseDto}.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<PaymentResponseDto>> getPaymentsByUserIdOrOrderIdOrStatus(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable
    ) {
        Page<PaymentResponseDto> paymentsPage = paymentClient.getPaymentsByUserIdOrOrderIdOrStatus(
                userId,
                orderId,
                status,
                pageable
        );
        return ResponseEntity.ok(paymentsPage);
    }

    /**
     * Calculates the total sum of payments for a specified period and/or user.
     *
     * @param start  start of the date range (inclusive, optional).
     * @param end    end of the date range (inclusive, optional).
     * @param userId optional filter by user ID.
     * @return {@link ResponseEntity} containing the total sum as a {@link Long}.
     */
    @GetMapping("/sum")
    public ResponseEntity<Long> getSumByDateRange(
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            @RequestParam(required = false) Long userId
    ) {
        Long sum = paymentClient.getSumByDateRange(start, end, userId);
        return ResponseEntity.ok(sum);
    }

    /**
     * Partially updates the status of an existing payment.
     *
     * @param id     the unique identifier of the payment.
     * @param status the new {@link PaymentStatus} to be applied.
     * @return {@link ResponseEntity} containing the updated {@link PaymentResponseDto}.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> changePaymentStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdatePaymentStatusRequest status
    ) {
        PaymentResponseDto paymentDto = paymentClient.changePaymentStatus(id, status);
        return ResponseEntity.ok(paymentDto);
    }

    /**
     * Removes a payment record from the system.
     *
     * @param id the unique identifier of the payment to be deleted.
     * @return {@link ResponseEntity} with HTTP 204 No Content status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        paymentClient.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}