package com.innowise.authservice.controller.user;

import com.innowise.authservice.client.CardClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.innowise.authservice.dto.PaymentCardDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CardController {

    private final CardClient cardClient;

    @PostMapping
    public ResponseEntity<PaymentCardDto> addPaymentCard(@Valid @RequestBody PaymentCardDto paymentCardDto) {
        PaymentCardDto paymentCard = cardClient.addPaymentCard(paymentCardDto);
        return new ResponseEntity<>(paymentCard, HttpStatus.CREATED);
    }
}
