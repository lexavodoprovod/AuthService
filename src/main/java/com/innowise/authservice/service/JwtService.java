package com.innowise.authservice.service;

import com.innowise.authservice.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for JSON Web Token (JWT) management.
 * Provides methods for token generation, parsing, and validation
 * to support secure authentication and authorization.
 */
public interface JwtService {

    /**
     * Generates a short-lived access token for the specified user.
     *
     * @param user the user entity containing ID, username, and role.
     * @return a signed JWT access token.
     */
    String generateAccessToken(User user);

    /**
     * Generates a long-lived refresh token for the specified user.
     *
     * @param user the user entity containing ID, username, and role.
     * @return a signed JWT refresh token.
     */
    String generateRefreshToken(User user);

    /**
     * Extracts the unique user identifier (subject) from the token.
     *
     * @param token the JWT token.
     * @return the user ID.
     */
    String extractUserId(String token);

    /**
     * Extracts the username claim from the token.
     *
     * @param token the JWT token.
     * @return the username.
     */
    String extractUsername(String token);

    /**
     * Extracts the user role claim from the token.
     *
     * @param token the JWT token.
     * @return the role name.
     */
    String extractUserRole(String token);

    /**
     * Validates the token for the API Gateway, checking both signature
     * and revocation status in the database.
     *
     * @param token the JWT token to validate.
     * @return {@code true} if the token is valid and not logged out, {@code false} otherwise.
     */
    boolean isValidTokenForGateway(String token);

    /**
     * Validates the access token against user details and checks its expiration
     * and database status.
     *
     * @param token the access token.
     * @param user the user details to compare with the token claims.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    boolean isValidAccessToken(String token, UserDetails user);

    /**
     * Validates the refresh token against user details and checks its expiration
     * and database status.
     *
     * @param token the refresh token.
     * @param user the user details to compare with the token claims.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    boolean isValidRefreshToken(String token, UserDetails user);
}
