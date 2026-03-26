package com.innowise.authservice.service.impl;

import com.innowise.authservice.entity.Role;
import com.innowise.authservice.entity.Token;
import com.innowise.authservice.entity.User;
import com.innowise.authservice.repository.TokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private TokenRepository  tokenRepository;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private User testUser;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 86400000L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("danik_popa");
        testUser.setRole(Role.USER);
    }

    @Nested
    @DisplayName("Generate access token tests")
    class GenerateAccessTokenTests{
        @Test
        void shouldGenerateValidToken() {
            String token = jwtService.generateAccessToken(testUser);

            assertNotNull(token);
            assertEquals("1", jwtService.extractUserId(token));
            assertEquals("danik_popa", jwtService.extractUsername(token));
            assertEquals("USER", jwtService.extractUserRole(token));
        }

        @Test
        @DisplayName("Should contain correct claims (ID, username, role) in the generated token")
        void shouldContainCorrectClaims() {
            Long expectedId = 1L;
            String expectedUsername = "danik_popa";
            Role expectedRole = Role.USER;


            String token = jwtService.generateAccessToken(testUser);

            assertEquals(String.valueOf(expectedId), jwtService.extractUserId(token), "Subject should match user ID");
            assertEquals(expectedUsername, jwtService.extractUsername(token), "Claim 'username' should match");
            assertEquals(expectedRole.name(), jwtService.extractUserRole(token), "Claim 'role' should match");
        }

        @Test
        void isValidAccessToken_ShouldReturnTrue_WhenTokenIsValid() {
            String token = jwtService.generateAccessToken(testUser);

            Token tokenEntity = new Token();
            tokenEntity.setLoggedOut(false);

            when(tokenRepository.findTokenByAccessToken(token)).thenReturn(Optional.of(tokenEntity));

            boolean isValid = jwtService.isValidAccessToken(token, testUser);

            assertTrue(isValid);
        }

        @Test
        void isValidAccessToken_ShouldReturnFalse_WhenTokenIsLoggedOut() {
            String token = jwtService.generateAccessToken(testUser);

            Token tokenEntity = new Token();
            tokenEntity.setLoggedOut(true);

            when(tokenRepository.findTokenByAccessToken(token)).thenReturn(Optional.of(tokenEntity));

            boolean isValid = jwtService.isValidAccessToken(token, testUser);

            assertFalse(isValid);
        }

        @Test
        void shouldThrowException_WhenTokenIsInvalid() {
            String invalidToken = "wrong.token.here";

            assertThrows(Exception.class, () -> jwtService.extractUsername(invalidToken));
        }
    }

    @Nested
    @DisplayName("Generate refresh token tests")
    class GenerateRefreshTokenTests {

        @Test
        @DisplayName("Should successfully generate a refresh token string")
        void shouldGenerateRefreshTokenString() {
            User user = createTestUser(1L, "danik_user", Role.USER);

            String refreshToken = jwtService.generateRefreshToken(user);

            assertNotNull(refreshToken);
        }

        @Test
        @DisplayName("Should contain correct identity claims in refresh token")
        void shouldContainCorrectClaims() {
            Long userId = 42L;
            String username = "refresh_tester";
            User user = createTestUser(userId, username, Role.USER);


            String token = jwtService.generateRefreshToken(user);


            assertEquals(String.valueOf(userId), jwtService.extractUserId(token));
            assertEquals(username, jwtService.extractUsername(token));
        }

        @Test
        @DisplayName("Refresh token should have a later expiration date than access token")
        void refreshTokenShouldHaveLongerExpiration() {
            User user = createTestUser(1L, "user", Role.USER);

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            Date accessExpiration = ReflectionTestUtils.invokeMethod(jwtService, "extractExpiration", accessToken);
            Date refreshExpiration = ReflectionTestUtils.invokeMethod(jwtService, "extractExpiration", refreshToken);

            assertNotNull(accessExpiration);
            assertNotNull(refreshExpiration);
            assertTrue(refreshExpiration.after(accessExpiration),
                    "Refresh token must expire later than access token");
        }

        @Test
        @DisplayName("Should return true when refresh token is valid and exists in DB")
        void isValidRefreshToken_ShouldReturnTrue_WhenValid() {
            User user = createTestUser(1L, "danik", Role.USER);
            String refreshToken = jwtService.generateRefreshToken(user);

            Token tokenEntity = new Token();
            tokenEntity.setLoggedOut(false);

            when(tokenRepository.findTokenByRefreshToken(refreshToken)).thenReturn(Optional.of(tokenEntity));

            boolean isValid = jwtService.isValidRefreshToken(refreshToken, user);

            assertTrue(isValid);
        }
    }

    @Nested
    @DisplayName("Extract claims tests")
    class ExtractClaimsTests {

        @Test
        @DisplayName("Should correctly extract user ID from valid token")
        void shouldExtractUserIdFromToken() {

            Long expectedId = 99L;
            User user = createTestUser(expectedId, "nikita", Role.USER);

            String token = jwtService.generateAccessToken(user);

            String extractedId = jwtService.extractUserId(token);

            assertNotNull(extractedId);
            assertEquals(String.valueOf(expectedId), extractedId, "Extracted ID must match the one in the token");
        }

        @Test
        @DisplayName("Should throw exception when extracting ID from malformed token")
        void shouldThrowExceptionForInvalidToken() {
            String malformedToken = "not.a.valid.jwt.token";


            assertThrows(Exception.class, () -> jwtService.extractUserId(malformedToken),
                    "Should throw an exception if the token is invalid or tampered with");
        }
    }

    @Nested
    @DisplayName("Extract custom claims tests")
    class ExtractCustomClaimsTests {

        @Test
        @DisplayName("Should correctly extract username from token claims")
        void shouldExtractUsernameFromToken() {
            String expectedUsername = "danik_popa";
            User user = createTestUser(1L, expectedUsername, Role.USER);
            String token = jwtService.generateAccessToken(user);

            String extractedUsername = jwtService.extractUsername(token);

            assertNotNull(extractedUsername);
            assertEquals(expectedUsername, extractedUsername, "The extracted username should match the user's username");
        }

        @Test
        @DisplayName("Should handle missing custom claim (role) gracefully")
        void extractUserRole_ShouldReturnNull_WhenClaimIsMissing() {
            String tokenWithoutRole = Jwts.builder()
                    .subject("1")
                    .claim("username", "test")
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)), Jwts.SIG.HS256)
                    .compact();

            String role = jwtService.extractUserRole(tokenWithoutRole);
            assertNull(role);
        }

        @Test
        @DisplayName("Should correctly extract user role from token claims")
        void shouldExtractUserRoleFromToken() {
            Role expectedRole = Role.ADMIN;
            User user = createTestUser(1L, "admin_user", expectedRole);
            String token = jwtService.generateAccessToken(user);

            String extractedRole = jwtService.extractUserRole(token);


            assertNotNull(extractedRole);
            assertEquals(expectedRole.name(), extractedRole, "The extracted role string should match the role name");
        }

        @Test
        @DisplayName("Should return null or throw exception if custom claim is missing")
        void shouldHandleMissingClaims() {

            String tokenWithoutRole = Jwts.builder()
                    .subject("1")
                    .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                    .compact();

            assertNull(jwtService.extractUserRole(tokenWithoutRole));
        }
    }

    @Nested
    @DisplayName("Is valid access token tests")
    class IsValidAccessTokenTests {

        @Test
        @DisplayName("Should return true when token is valid, not expired and not logged out")
        void shouldReturnTrueWhenTokenIsValid() {
            User userEntity = createTestUser(1L, "nikita", Role.USER);
            String token = jwtService.generateAccessToken(userEntity);

            Token dbToken = new Token();
            dbToken.setLoggedOut(false);

            when(tokenRepository.findTokenByAccessToken(token)).thenReturn(Optional.of(dbToken));

            boolean isValid = jwtService.isValidAccessToken(token, userEntity);

            assertTrue(isValid, "Token should be valid");
            verify(tokenRepository).findTokenByAccessToken(token);
        }

        @Test
        @DisplayName("Should return false when username in token does not match user details")
        void shouldReturnFalseWhenUsernameMismatch() {
            User userInToken = createTestUser(1L, "correct_user", Role.USER);
            User stranger = createTestUser(2L, "stranger", Role.USER);
            String token = jwtService.generateAccessToken(userInToken);

            boolean isValid = jwtService.isValidAccessToken(token, stranger);

            assertFalse(isValid, "Should be invalid if username doesn't match");
        }

        @Test
        @DisplayName("Should return false when token is marked as logged out in database")
        void shouldReturnFalseWhenTokenIsLoggedOut() {
            User user = createTestUser(1L, "nikita", Role.USER);
            String token = jwtService.generateAccessToken(user);

            Token dbToken = new Token();
            dbToken.setLoggedOut(true);

            when(tokenRepository.findTokenByAccessToken(token)).thenReturn(Optional.of(dbToken));

            boolean isValid = jwtService.isValidAccessToken(token, user);

            assertFalse(isValid, "Should be invalid if token is revoked (logged out)");
        }

        @Test
        @DisplayName("Should return false when token is missing from database")
        void shouldReturnFalseWhenTokenNotFoundInDb() {
            User user = createTestUser(1L, "nikita", Role.USER);
            String token = jwtService.generateAccessToken(user);

            when(tokenRepository.findTokenByAccessToken(token)).thenReturn(Optional.empty());

            boolean isValid = jwtService.isValidAccessToken(token, user);

            assertFalse(isValid, "Should be invalid if token is not tracked in DB");
        }

        @Test
        @DisplayName("Should return false when token is expired")
        void isValidAccessToken_ShouldReturnFalse_WhenExpired() {

            ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", -60000L);
            String expiredToken = jwtService.generateAccessToken(testUser);

            ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 3600000L);


            assertThrows(RuntimeException.class, () -> jwtService.isValidAccessToken(expiredToken, testUser));
        }
    }


    private User createTestUser(Long id, String username, Role role) {
        User user = User.builder()
                .id(id)
                .username(username)
                .role(role)
                .build();
        return user;
    }




}