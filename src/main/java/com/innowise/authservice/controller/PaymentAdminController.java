package com.innowise.authservice.controller;

import com.innowise.authservice.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping(value = "/admin/payments", produces = "application/json")
@RequiredArgsConstructor
public class PaymentAdminController {

    /**
     * Records a new payment in the system.
     *
     * @param paymentDto the payment data transfer object.
     * @return {@link ResponseEntity} containing the created {@link PaymentDto}.
     */
    @PostMapping
    public ResponseEntity<PaymentDto> addPayment(@RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok().body(new PaymentDto());
    }

    /**
     * Retrieves a paginated history of all payments.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link PaymentDto}.
     */
    @GetMapping
    public ResponseEntity<Page<PaymentDto>> getAllPayments() {
        return ResponseEntity.ok().body(Page.empty());
    }

    /**
     * Updates a payment record.
     *
     * @param id         the unique identifier of the payment.
     * @param paymentDto updated payment details.
     * @return {@link ResponseEntity} containing the updated {@link PaymentDto}.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable Long id, @RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok().body(new PaymentDto());
    }

    /**
     * Deletes a payment record from the system.
     *
     * @param id the unique identifier of the payment.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
