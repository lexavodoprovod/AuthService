package com.innowise.authservice.controller;

import com.innowise.authservice.dto.AuthenticationResponseDto;
import com.innowise.authservice.dto.LoginDto;
import com.innowise.authservice.dto.RegistrationDto;
import com.innowise.authservice.service.AuthenticationService;
import com.innowise.authservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.innowise.authservice.constant.TokenInfo.*;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/registration")
    public ResponseEntity<String> register(@RequestBody RegistrationDto registrationDto) {
        Long id = authenticationService.register(registrationDto);
        return ResponseEntity.ok().body("User registered successfully with id: %s".formatted(id));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authenticationService.authenticate(loginDto));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        return authenticationService.refreshToken(request, response);
    }

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
