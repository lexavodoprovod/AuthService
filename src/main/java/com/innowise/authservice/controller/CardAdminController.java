package com.innowise.authservice.controller;

import com.innowise.authservice.client.CardClient;
import com.innowise.authservice.dto.PaymentCardDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CardAdminController {

    private final CardClient paymentCardClient;


    /**
     * Retrieves a paginated list of all payment cards in the system.
     *
     * @param number   optional filter by card number.
     * @param pageable pagination and sorting information.
     * @return {@link ResponseEntity} containing a {@link Page} of {@link PaymentCardDto}.
     */
    @GetMapping
    public ResponseEntity<Page<PaymentCardDto>> getAllCards(@RequestParam(required = false) String number,
                                                            Pageable pageable) {
        Page<PaymentCardDto> cardDtoPage = paymentCardClient.getAllPaymentCards(number, pageable);
        return ResponseEntity.ok(cardDtoPage);
    }


    /**
     * Retrieves details of a specific payment card.
     *
     * @param id the unique identifier of the card.
     * @return {@link ResponseEntity} containing the {@link PaymentCardDto}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardDto> getPaymentCardById(@PathVariable Long id) {

        PaymentCardDto paymentCard = paymentCardClient.getPaymentCardById(id);

        return ResponseEntity.ok(paymentCard);
    }

    /**
     * Updates payment card information.
     *
     * @param id             the unique identifier of the card to update.
     * @param paymentCardDto the {@link PaymentCardDto} containing updated details.
     * @return {@link ResponseEntity} containing the updated {@link PaymentCardDto}.
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<PaymentCardDto> updatePaymentCard(@PathVariable Long id, @Valid @RequestBody PaymentCardDto paymentCardDto) {
        paymentCardDto.setId(id);

        PaymentCardDto paymentCard = paymentCardClient.updatePaymentCard(id, paymentCardDto);
        return ResponseEntity.ok(paymentCard);
    }

    /**
     * Activates a specific payment card.
     *
     * @param id the unique identifier of the card.
     * @return {@link ResponseEntity} with no content on success.
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long id) {
        return paymentCardClient.activatePaymentCard(id);
    }

    /**
     * Deactivates (soft deletes) a specific payment card.
     *
     * @param id the unique identifier of the card.
     * @return {@link ResponseEntity} with no content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateCard(@PathVariable Long id) {
        return paymentCardClient.deactivatePaymentCard(id);
    }
}
