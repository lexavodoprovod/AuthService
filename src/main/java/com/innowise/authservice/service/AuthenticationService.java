package com.innowise.authservice.service;

import com.innowise.authservice.dto.AuthenticationResponseDto;
import com.innowise.authservice.dto.LoginDto;
import com.innowise.authservice.dto.RegistrationDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for handling user authentication, registration,
 * and token management operations.
 */
public interface AuthenticationService {

    /**
     * Registers a new user in the system.
     * Validates input data, communicates with the User Microservice via Feign client,
     * and saves authentication credentials.
     *
     * @param registrationDto the data transfer object containing registration details.
     * @return the unique identifier (ID) of the newly created user.
     * @throws com.innowise.authservice.exception.NullParameterException if registrationDto is null.
     * @throws com.innowise.authservice.exception.ExistUserException if the username is already taken.
     */
    Long register(RegistrationDto registrationDto);

    /**
     * Authenticates a user based on username and password.
     * Generates a new pair of access and refresh tokens upon successful authentication.
     *
     * @param loginDto the data transfer object containing login credentials.
     * @return {@link AuthenticationResponseDto} containing access and refresh tokens.
     * @throws com.innowise.authservice.exception.NullParameterException if loginDto is null.
     * @throws com.innowise.authservice.exception.UserNotFoundException if the user does not exist.
     */
    AuthenticationResponseDto authenticate(LoginDto loginDto);

    /**
     * Refreshes the authentication tokens using a valid refresh token from the request header.
     * Revokes old tokens and issues a new pair to maintain security.
     *
     * @param request  the {@link HttpServletRequest} containing the Authorization header.
     * @return {@link ResponseEntity} containing the new {@link AuthenticationResponseDto}
     *         or an Unauthorized status if the token is invalid.
     */
    ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request);

}
