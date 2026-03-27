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

/**
 * Controller providing endpoints for authenticated users to manage their own data.
 * Accesses user information and payment cards by extracting user identity from JWT.
 */
@RestController
@RequestMapping(value = "/user", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;
    private final JwtService jwtService;

    /**
     * Retrieves the profile information of the currently authenticated user.
     * Extracts user ID from the provided JWT token.
     *
     * @param authHeader the Authorization header containing the JWT token.
     * @return {@link ResponseEntity} containing the user's profile data as {@link UserDto}.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(@RequestHeader(JWT_HEADER_NAME) String authHeader) {
        String token = authHeader.substring(JWT_HEADER_PREFIX.length());
        Long userId = Long.parseLong(jwtService.extractUserId(token));

        UserDto userDto = userClient.getUserById(userId);

       return ResponseEntity.ok(userDto);
    }

    /**
     * Retrieves all payment cards associated with the currently authenticated user.
     * Extracts user ID from the provided JWT token.
     *
     * @param authHeader the Authorization header containing the JWT token.
     * @return {@link ResponseEntity} containing a list of {@link PaymentCardDto}.
     */
    @GetMapping("/payment-cards")
    public ResponseEntity<List<PaymentCardDto>> getMyCards(@RequestHeader(JWT_HEADER_NAME) String authHeader) {
        String token = authHeader.substring(JWT_HEADER_PREFIX.length());
        Long userId = Long.parseLong(jwtService.extractUserId(token));

        List<PaymentCardDto> cards = userClient.getAllPaymentCardsByUserId(userId);

        return ResponseEntity.ok(cards);
    }

    /**
     * Updates the profile information for the currently authenticated user.
     * Extracts user ID from the JWT token and applies updates from the request body.
     *
     * @param authHeader the Authorization header containing the JWT token.
     * @param userDto    the {@link UserDto} containing updated user information.
     * @return {@link ResponseEntity} containing the updated user profile data.
     */
    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestHeader(JWT_HEADER_NAME) String authHeader, @RequestBody UserDto userDto) {
        String token = authHeader.substring(JWT_HEADER_PREFIX.length());
        Long userId = Long.parseLong(jwtService.extractUserId(token));

        UserDto updatedUserDto = userClient.updateUser(userId, userDto);

        return ResponseEntity.ok(updatedUserDto);
    }

}
