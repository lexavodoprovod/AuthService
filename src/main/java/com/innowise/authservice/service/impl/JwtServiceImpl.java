package com.innowise.authservice.service.impl;

import com.innowise.authservice.entity.User;
import com.innowise.authservice.repository.TokenRepository;
import com.innowise.authservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final String USERNAME = "username";
    private static final String ROLE = "role";

    private final TokenRepository tokenRepository;

    @Value("${SECRET_KEY}")
    private String secretKey;

    @Value("${ACCESS_TOKEN_EXPIRATION}")
    private long accessTokenExpiration;

    @Value("${REFRESH_TOKEN_EXPIRATION}")
    private long refreshTokenExpiration;


    @Override
    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpiration);
    }

    @Override
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get(USERNAME, String.class));
    }

    @Override
    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get(ROLE, String.class));
    }

    @Override
    public boolean isValidAccessToken(String token, UserDetails user) {

       String username = extractUsername(token);

        boolean isValidToken = tokenRepository.findTokenByAccessToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getUsername())
                && isTokenExpired(token)
                && isValidToken;
    }

    @Override
    public boolean isValidRefreshToken(String token, UserDetails user) {
        String username = extractUsername(token);

        boolean isValidToken = tokenRepository.findTokenByRefreshToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getUsername())
                && isTokenExpired(token)
                && isValidToken;
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(User user, long expiryTime){
        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim(USERNAME, user.getUsername())
                .claim(ROLE, user.getRole().name())
                .claim("jti", UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSigningKey(), Jwts.SIG.HS512);
        return builder.compact();
    }

    private Claims extractAllClaims(String token){
        JwtParserBuilder parser = Jwts.parser();
        parser.verifyWith(getSigningKey());
        return parser.build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private  <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return !extractExpiration(token).before(new Date());
    }
}
