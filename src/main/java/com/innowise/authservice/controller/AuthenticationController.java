package com.innowise.authservice.controller;

import com.innowise.authservice.dto.AuthenticationResponseDto;
import com.innowise.authservice.dto.LoginDto;
import com.innowise.authservice.dto.RegistrationDto;
import com.innowise.authservice.service.AuthenticationService;
import com.innowise.authservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.innowise.authservice.constant.TokenInfo.*;

/**
 * REST controller for authentication operations.
 * Handles user registration, login, token refreshing, and token validation.
 */
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    /**
     * Registers a new user in the system.
     *
     * @param registrationDto the user data for registration.
     * @return {@link ResponseEntity} with a success message containing the new user ID.
     */
    @PostMapping("/registration")
    public ResponseEntity<String> register(@RequestBody RegistrationDto registrationDto) {
        Long id = authenticationService.register(registrationDto);
        return ResponseEntity.ok().body("User registered successfully with id: %s".formatted(id));
    }

    /**
     * Authenticates a user and issues JWT tokens.
     *
     * @param loginDto the user's credentials (username and password).
     * @return {@link ResponseEntity} containing access and refresh tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authenticationService.authenticate(loginDto));
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param request  the HTTP request containing the refresh token in the headers.
     * @return {@link ResponseEntity} with a new pair of JWT tokens.
     */
    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request) {

        return authenticationService.refreshToken(request);
    }

    /**
     * Validates a JWT token for the API Gateway.
     * Checks if the token is properly signed, not expired, and not revoked (logged out).
     *
     * @param authHeader the Authorization header containing the JWT token with Bearer prefix.
     * @return 200 OK if the token is valid, or 401 Unauthorized if validation fails.
     */
    @GetMapping("/validate")
    public ResponseEntity<Void> validate(
            @RequestHeader(JWT_HEADER_NAME)
            String authHeader){

        if (authHeader == null || !authHeader.startsWith(JWT_HEADER_PREFIX)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(JWT_HEADER_PREFIX.length());

        boolean isValid = jwtService.isValidTokenForGateway(token);

        return isValid ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
}
