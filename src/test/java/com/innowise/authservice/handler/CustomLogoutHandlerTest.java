package com.innowise.authservice.handler;

import com.innowise.authservice.entity.Token;
import com.innowise.authservice.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static com.innowise.authservice.constant.TokenInfo.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomLogoutHandlerTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CustomLogoutHandler logoutHandler;

    @Test
    @DisplayName("Logout - Should return early when Authorization header is missing")
    void shouldReturnEarlyWhenHeaderIsMissing() {
        when(request.getHeader(JWT_HEADER_NAME)).thenReturn(null);

        logoutHandler.logout(request, response, authentication);

        verifyNoInteractions(tokenRepository);
    }

    @Test
    @DisplayName("Logout - Should return early when Header does not start with Bearer")
    void shouldReturnEarlyWhenHeaderIsInvalid() {
        when(request.getHeader(JWT_HEADER_NAME)).thenReturn("InvalidPrefix token123");

        logoutHandler.logout(request, response, authentication);

        verifyNoInteractions(tokenRepository);
    }

    @Test
    @DisplayName("Logout - Should do nothing when token is not found in repository")
    void shouldDoNothingWhenTokenNotFoundInDb() {
        String rawToken = "validToken";
        String fullHeader = JWT_HEADER_PREFIX + rawToken;
        when(request.getHeader(JWT_HEADER_NAME)).thenReturn(fullHeader);
        when(tokenRepository.findTokenByAccessToken(rawToken)).thenReturn(Optional.empty());

        logoutHandler.logout(request, response, authentication);

        verify(tokenRepository, times(1)).findTokenByAccessToken(rawToken);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Logout - Should set loggedOut to true and save token when found")
    void shouldLogoutSuccessfully() {
        String rawToken = "secretJwt";
        String fullHeader = JWT_HEADER_PREFIX + rawToken;
        Token storedToken = new Token();
        storedToken.setLoggedOut(false);

        when(request.getHeader(JWT_HEADER_NAME)).thenReturn(fullHeader);
        when(tokenRepository.findTokenByAccessToken(rawToken)).thenReturn(Optional.of(storedToken));

        logoutHandler.logout(request, response, authentication);

        assert(storedToken.isLoggedOut());
        verify(tokenRepository).save(storedToken);
    }
}