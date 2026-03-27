package com.innowise.authservice.service;

import com.innowise.authservice.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String extractUserId(String token);

    String extractUsername(String token);

    String extractUserRole(String token);

    boolean isValidTokenForGateway(String token);

    boolean isValidAccessToken(String token, UserDetails user);

    boolean isValidRefreshToken(String token, UserDetails user);
}
