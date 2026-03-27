package com.innowise.authservice.controller;

import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.innowise.authservice.constant.TokenInfo.*;


import java.util.List;

@RestController
@RequestMapping(value = "/user", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;
    private final JwtService jwtService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(@RequestHeader(JWT_HEADER_NAME) String authHeader) {
        String token = authHeader.substring(JWT_HEADER_PREFIX.length());
        Long userId = Long.parseLong(jwtService.extractUserId(token));

        UserDto userDto = userClient.getUserById(userId);

       return ResponseEntity.ok(userDto);
    }

    @GetMapping("/payment-cards")
    public ResponseEntity<List<PaymentCardDto>> getMyCards(@RequestHeader(JWT_HEADER_NAME) String authHeader) {
        String token = authHeader.substring(JWT_HEADER_PREFIX.length());
        Long userId = Long.parseLong(jwtService.extractUserId(token));

        List<PaymentCardDto> cards = userClient.getAllPaymentCardsByUserId(userId);

        return ResponseEntity.ok(cards);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestHeader(JWT_HEADER_NAME) String authHeader, @RequestBody UserDto userDto) {
        String token = authHeader.substring(JWT_HEADER_PREFIX.length());
        Long userId = Long.parseLong(jwtService.extractUserId(token));

        UserDto updatedUserDto = userClient.updateUser(userId, userDto);

        return ResponseEntity.ok(updatedUserDto);
    }

}
