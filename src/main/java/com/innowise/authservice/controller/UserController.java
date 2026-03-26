package com.innowise.authservice.controller;

import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        UserDto userDto = userClient.getUserById(userId);

       return ResponseEntity.ok(userDto);
    }

    @GetMapping("/payment-cards")
    public ResponseEntity<List<PaymentCardDto>> getMyCards(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        List<PaymentCardDto> cards = userClient.getAllPaymentCardsByUserId(userId);

        return ResponseEntity.ok(cards);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(Authentication authentication, @RequestBody UserDto userDto) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        UserDto updatedUserDto = userClient.updateUser(userId, userDto);
        return ResponseEntity.ok(updatedUserDto);
    }

}
