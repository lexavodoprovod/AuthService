package com.innowise.authservice.controller;

import com.innowise.authservice.client.PaymentCardClient;
import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin", produces = "application/json")
@RequiredArgsConstructor
public class AdminController {

    private final UserClient userClient;
    private final PaymentCardClient paymentCardClient;

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {

        UserDto userDto = userClient.getUserById(id);

        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users/{id}/cards")
    public ResponseEntity<List<PaymentCardDto>> getMyCards(@PathVariable Long id) {
        List<PaymentCardDto> cards = userClient.getAllPaymentCardsByUserId(id);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam(required = false) String name,
                                                     @RequestParam(required = false) String surname,
                                                     Pageable pageable) {
        Page<UserDto> userDtoPage = userClient.getAllUsers(name, surname, pageable);
       return ResponseEntity.ok(userDtoPage);
    }

    @PutMapping("/users/{id}/update")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto updatedUserDto = userClient.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUserDto);
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        return userClient.activateUser(id);
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        return userClient.deactivateUser(id);
    }

    @GetMapping("/cards")
    public ResponseEntity<Page<PaymentCardDto>> getAllCards(@RequestParam(required = false) String number,
                                                            Pageable pageable) {
        Page<PaymentCardDto> cardDtoPage = paymentCardClient.getAllPaymentCards(number, pageable);
        return ResponseEntity.ok(cardDtoPage);
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<PaymentCardDto> getPaymentCardById(@PathVariable Long id) {

        PaymentCardDto paymentCard = paymentCardClient.getPaymentCardById(id);

        return ResponseEntity.ok(paymentCard);
    }

    @PutMapping("/cards/{id}/update")
    public ResponseEntity<PaymentCardDto> updatePaymentCard(@PathVariable Long id, @Valid @RequestBody PaymentCardDto paymentCardDto) {
        paymentCardDto.setId(id);

        PaymentCardDto paymentCard = paymentCardClient.updatePaymentCard(id, paymentCardDto);
        return ResponseEntity.ok(paymentCard);
    }

    @PatchMapping("/cards/{id}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long id) {
        return paymentCardClient.activatePaymentCard(id);
    }

    @PatchMapping("/cards/{id}/deactivate")
    public ResponseEntity<Void> deactivateCard(@PathVariable Long id) {
        return paymentCardClient.deactivatePaymentCard(id);
    }
}
