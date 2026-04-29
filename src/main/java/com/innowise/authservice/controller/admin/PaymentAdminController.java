package com.innowise.authservice.controller.admin;

import com.innowise.authservice.client.PaymentClient;
import com.innowise.authservice.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping(value = "/admin/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentAdminController {

    private final PaymentClient paymentClient;

    /**
     * Records a new payment in the system.
     *
     * @param paymentFromAdmin the payment data transfer object.
     * @return {@link ResponseEntity} containing the created {@link PaymentDto}.
     */
    @PostMapping
    public ResponseEntity<PaymentDto> addPayment(@RequestBody PaymentDto paymentFromAdmin) {

        PaymentDto paymentDto =  paymentClient.addPayment(paymentFromAdmin);

        return ResponseEntity.ok(paymentDto);
    }

    /**
     * Retrieves a paginated history of all payments.
     *
     * @return {@link ResponseEntity} containing a {@link Page} of {@link PaymentDto}.
     */
    @GetMapping
    public ResponseEntity<Page<PaymentDto>> getAllPayments() {

        Page<PaymentDto> paymentDtoPage = paymentClient.getAllPayments();

        return ResponseEntity.ok(paymentDtoPage);
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
        PaymentDto updatedPaymentDto = paymentClient.updatePayment(id, paymentDto);
        return ResponseEntity.ok(updatedPaymentDto);
    }

    /**
     * Deletes a payment record from the system.
     *
     * @param id the unique identifier of the payment.
     * @return {@link ResponseEntity} with 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        return paymentClient.deletePayment(id);
    }
}
