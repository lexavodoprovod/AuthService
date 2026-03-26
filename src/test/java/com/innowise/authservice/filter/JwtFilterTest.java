package com.innowise.authservice.filter;

import com.innowise.authservice.service.JwtService;
import com.innowise.authservice.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static com.innowise.authservice.constant.TokenInfo.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should skip filter when no Authorization header")
    void shouldSkipFilter_WhenNoAuthHeader() throws ServletException, IOException {
        when(request.getHeader(JWT_HEADER_NAME)).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should skip filter when header does not start with Bearer")
    void shouldSkipFilter_WhenInvalidHeaderPrefix() throws ServletException, IOException {
        when(request.getHeader(JWT_HEADER_NAME)).thenReturn("Basic dXNlcjpwYXNz");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
    }

    @Test
    @DisplayName("Should authenticate when token is valid")
    void shouldAuthenticate_WhenTokenIsValid() throws ServletException, IOException {
        String token = "valid.token.here";
        String username = "test_user";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader(JWT_HEADER_NAME)).thenReturn(JWT_HEADER_PREFIX + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isValidAccessToken(token, userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(null); // или пустой список

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate when token is invalid")
    void shouldNotAuthenticate_WhenTokenIsInvalid() throws ServletException, IOException {
        String token = "invalid.token";
        String username = "test_user";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader(JWT_HEADER_NAME)).thenReturn(JWT_HEADER_PREFIX + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isValidAccessToken(token, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}