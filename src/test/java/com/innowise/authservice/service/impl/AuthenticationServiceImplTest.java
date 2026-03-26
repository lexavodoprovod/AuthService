package com.innowise.authservice.service.impl;

import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.AuthenticationResponseDto;
import com.innowise.authservice.dto.LoginDto;
import com.innowise.authservice.dto.RegistrationDto;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.entity.Role;
import com.innowise.authservice.entity.Token;
import com.innowise.authservice.entity.User;
import com.innowise.authservice.exception.ExistUserException;
import com.innowise.authservice.exception.NullParameterException;
import com.innowise.authservice.exception.UserNotFoundException;
import com.innowise.authservice.repository.TokenRepository;
import com.innowise.authservice.repository.UserRepository;
import com.innowise.authservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import static com.innowise.authservice.constant.TokenInfo.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserClient userClient;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;


    @Nested
    @DisplayName("Register tests")
    class RegisterTests {

        @Test
        @DisplayName("Should successfully register a new user")
        void shouldRegisterUserSuccessfully() {
            RegistrationDto regDto = RegistrationDto.builder()
                    .username("danik")
                    .password("pass123")
                    .name("Danik")
                    .build();

            UserDto userResponseDto = UserDto.builder().id(10L).build();

            when(userRepository.existsByUsername("danik")).thenReturn(false);
            when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
            when(userClient.addUser(any(UserDto.class))).thenReturn(userResponseDto);

            Long resultId = authenticationService.register(regDto);

            assertEquals(10L, resultId);
            verify(userClient).addUser(any(UserDto.class));
            verify(userRepository).save(any(User.class));
            verify(passwordEncoder).encode("pass123");
        }



        @Test
        @DisplayName("Should throw NullParameterException when dto is null")
        void shouldThrowExceptionWhenDtoIsNull() {
            assertThrows(NullParameterException.class, () -> authenticationService.register(null));
        }

        @Test
        @DisplayName("Should throw ExistUserException when username already exists")
        void shouldThrowExceptionWhenUserExists() {
            RegistrationDto regDto = RegistrationDto.builder().username("existing_user").build();
            when(userRepository.existsByUsername("existing_user")).thenReturn(true);

            assertThrows(ExistUserException.class, () -> authenticationService.register(regDto));
            verify(userRepository, never()).save(any());
            verify(userClient, never()).addUser(any());
        }

        @Test
        @DisplayName("Should correctly map all fields from RegistrationDto to UserDto")
        void shouldMapAllFieldsCorrectly() {
            RegistrationDto regDto = RegistrationDto.builder()
                    .username("danik")
                    .password("123")
                    .name("Nikita")
                    .surname("Hololeenko")
                    .email("danik@mail.com")
                    .build();

            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(userClient.addUser(any(UserDto.class))).thenReturn(UserDto.builder().id(1L).build());

            authenticationService.register(regDto);

            org.mockito.ArgumentCaptor<UserDto> captor = org.mockito.ArgumentCaptor.forClass(UserDto.class);
            verify(userClient).addUser(captor.capture());

            UserDto captured = captor.getValue();
            assertEquals("Nikita", captured.getName());
            assertEquals("Hololeenko", captured.getSurname());
            assertEquals("danik@mail.com", captured.getEmail());
        }
    }

    @Nested
    @DisplayName("Authenticate tests")
    class AuthenticateTests {

        @Test
        @DisplayName("Should successfully authenticate user and return tokens")
        void shouldAuthenticateSuccessfully() {
            LoginDto loginDto = new LoginDto("danik", "password123");
            User user = User.builder()
                    .id(1L)
                    .username("danik")
                    .role(Role.USER)
                    .build();

            String accessToken = "access_token_123";
            String refreshToken = "refresh_token_456";

            when(userRepository.findByUsername("danik")).thenReturn(Optional.of(user));
            when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
            when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(null);

            AuthenticationResponseDto response = authenticationService.authenticate(loginDto);

            assertNotNull(response);
            assertEquals(accessToken, response.getAccessToken());
            assertEquals(refreshToken, response.getRefreshToken());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(userRepository).findByUsername("danik");
            verify(jwtService).generateAccessToken(user);
            verify(jwtService).generateRefreshToken(user);


            verify(tokenRepository).findAllAccessTokensByUserId(user.getId());
            verify(tokenRepository).save(any(Token.class));
        }

        @Test
        @DisplayName("Should throw NullParameterException when loginDto is null")
        void shouldThrowExceptionWhenLoginDtoIsNull() {
            assertThrows(NullParameterException.class, () -> authenticationService.authenticate(null));
        }



        @Test
        @DisplayName("Should not fail when revoking tokens if user has no previous tokens")
        void shouldNotFailWhenUserHasNoTokens() {
            LoginDto loginDto = new LoginDto("danik", "password");
            User user = User.builder().id(1L).username("danik").build();

            when(userRepository.findByUsername("danik")).thenReturn(Optional.of(user));
            when(tokenRepository.findAllAccessTokensByUserId(1L)).thenReturn(java.util.Collections.emptyList());
            when(jwtService.generateAccessToken(user)).thenReturn("at");
            when(jwtService.generateRefreshToken(user)).thenReturn("rt");

            authenticationService.authenticate(loginDto);

            verify(tokenRepository).saveAll(java.util.Collections.emptyList());
            verify(tokenRepository).save(any(Token.class)); // Новый токен всё равно должен сохраниться
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist after authentication")
        void shouldThrowExceptionWhenUserNotFound() {
            LoginDto loginDto = new LoginDto("ghost", "password");
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(loginDto));
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when authentication fails")
        void shouldThrowExceptionWhenCredentialsAreInvalid() {
            LoginDto loginDto = new LoginDto("danik", "wrong_pass");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid password"));

            assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(loginDto));

            verify(jwtService, never()).generateAccessToken(any());
        }
    }

    @Nested
    @DisplayName("Refresh token tests")
    class RefreshTokenTests {

        @Test
        @DisplayName("Should successfully refresh tokens when refresh token is valid")
        void shouldRefreshTokenSuccessfully() {
            String oldRefreshToken = "old_refresh_token";
            String newAccessToken = "new_access_token";
            String newRefreshToken = "new_refresh_token";
            String username = "nikita_dev";

            User user = User.builder().id(1L).username(username).build();

            when(request.getHeader(JWT_HEADER_NAME)).thenReturn(JWT_HEADER_PREFIX + oldRefreshToken);
            when(jwtService.extractUsername(oldRefreshToken)).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            when(jwtService.isValidRefreshToken(oldRefreshToken, user)).thenReturn(true);

            when(jwtService.generateAccessToken(user)).thenReturn(newAccessToken);
            when(jwtService.generateRefreshToken(user)).thenReturn(newRefreshToken);

            ResponseEntity<AuthenticationResponseDto> responseEntity = authenticationService.refreshToken(request, response);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
            assertEquals(newAccessToken, responseEntity.getBody().getAccessToken());
            assertEquals(newRefreshToken, responseEntity.getBody().getRefreshToken());

            verify(tokenRepository).findAllAccessTokensByUserId(user.getId());
            verify(tokenRepository).save(any(Token.class));
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException during refresh if user disappears from DB")
        void shouldThrowExceptionWhenUserNotFoundDuringRefresh() {
            String token = "valid_token";
            String username = "ghost";

            when(request.getHeader(JWT_HEADER_NAME)).thenReturn(JWT_HEADER_PREFIX + token);
            when(jwtService.extractUsername(token)).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                    () -> authenticationService.refreshToken(request, response));
        }

        @Test
        @DisplayName("Should return 401 when Authorization header is missing or invalid")
        void shouldReturnUnauthorizedWhenHeaderIsMissing() {
            when(request.getHeader(JWT_HEADER_NAME)).thenReturn(null);

            ResponseEntity<AuthenticationResponseDto> responseEntity = authenticationService.refreshToken(request, response);

            assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
            verify(jwtService, never()).extractUsername(anyString());
        }

        @Test
        @DisplayName("Should return 401 when refresh token is expired or invalid")
        void shouldReturnUnauthorizedWhenTokenIsInvalid() {
            String invalidToken = "invalid_token";
            String username = "nikita";
            User user = User.builder().username(username).build();

            when(request.getHeader(JWT_HEADER_NAME)).thenReturn(JWT_HEADER_PREFIX + invalidToken);
            when(jwtService.extractUsername(invalidToken)).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            when(jwtService.isValidRefreshToken(invalidToken, user)).thenReturn(false);

            ResponseEntity<AuthenticationResponseDto> responseEntity = authenticationService.refreshToken(request, response);

            assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
            verify(jwtService, never()).generateAccessToken(any());
        }
    }

}