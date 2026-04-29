package com.innowise.authservice.client;

import com.innowise.authservice.client.fallback.UserClientFallBack;
import com.innowise.authservice.dto.PaymentCardDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.innowise.authservice.constant.PaginationSettings.*;
@FeignClient(name = "user-service",
            contextId = "cardClient",
             path = "/payment-cards",
             fallback = UserClientFallBack.class)
public interface CardClient {


    @PostMapping
    PaymentCardDto addPaymentCard(@Valid @RequestBody PaymentCardDto paymentCardDto);

    @GetMapping
   Page<PaymentCardDto> getAllPaymentCards(
            @RequestParam(required = false) String number,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable);

    @GetMapping("/{id}")
    PaymentCardDto getPaymentCardById(@PathVariable Long id);

    @PutMapping("/{id}")
    PaymentCardDto updatePaymentCard(
            @PathVariable Long id,
            @Valid @RequestBody PaymentCardDto paymentCardDto);


    @PatchMapping("/{id}/activate")
    ResponseEntity<Void> activatePaymentCard(@PathVariable Long id);

    @DeleteMapping("/{id}/deactivate")
    ResponseEntity<Void> deactivatePaymentCard(@PathVariable Long id);
}
