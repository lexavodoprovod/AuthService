package com.innowise.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.entity.Role;
import com.innowise.authservice.entity.Token;
import com.innowise.authservice.entity.User;
import com.innowise.authservice.exception.UserServiceException;
import com.innowise.authservice.repository.TokenRepository;
import com.innowise.authservice.repository.UserRepository;
import com.innowise.authservice.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static com.innowise.authservice.constant.TokenInfo.*;


import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends BaseIT{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserClient userClient;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        tokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("Get My Profile Tests")
    class GetMyProfileTests {

        @Test
        @DisplayName("Should return user profile when authenticated")
        void shouldReturnProfileWhenAuthenticated() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);

            UserDto expectedDto = UserDto.builder()
                    .id(userId)
                    .name("Nikita")
                    .email("nikita@example.com")
                    .build();

            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;

            when(userClient.getUserById(userId)).thenReturn(expectedDto);

            mockMvc.perform(get("/user/profile")
                            .header(JWT_HEADER_NAME, bearerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId))
                    .andExpect(jsonPath("$.name").value("Nikita"))
                    .andExpect(jsonPath("$.email").value("nikita@example.com"));

            verify(userClient, times(1)).getUserById(userId);
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/user/profile"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(userClient);
        }

        @Test
        @DisplayName("GET /user/profile - Failure: should return 500 when UserClient fails")
        void shouldReturnInternalServerError_WhenUserClientFails() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);

            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;

            when(userClient.getUserById(userId))
                    .thenThrow(new UserServiceException());

            mockMvc.perform(get("/user/profile")
                            .header(JWT_HEADER_NAME, bearerToken)
                            .with(user(mockUser)))
                    .andExpect(status().isServiceUnavailable());

            verify(userClient, times(1)).getUserById(userId);
        }

        @Test
        @DisplayName("GET /user/profile - Success: should handle empty response from client")
        void shouldHandleNullProfileFromClient() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);

            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;


            when(userClient.getUserById(userId)).thenReturn(null);

            mockMvc.perform(get("/user/profile")
                            .header(JWT_HEADER_NAME, bearerToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("GET /user/profile - Success: Admin should also see his profile")
        void shouldReturnProfileForAdmin() throws Exception {
            Long userId = 1L;
            User admin = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);

            UserDto adminDto = UserDto.builder()
                    .id(userId)
                    .name("admin")
                    .email("nikita@example.com")
                    .build();

            String accessToken = jwtService.generateAccessToken(admin);
            String refreshToken = jwtService.generateRefreshToken(admin);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(admin);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;


            when(userClient.getUserById(userId)).thenReturn(adminDto);

            mockMvc.perform(get("/user/profile")
                            .header(JWT_HEADER_NAME, bearerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("admin"));
        }
    }

    @Nested
    @DisplayName("Get My Payment Cards Tests")
    class GetMyCardsTests {

        @Test
        @DisplayName("Should return list of cards when user has them")
        void shouldReturnCardsWhenAuthenticated() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);

            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;

            List<PaymentCardDto> mockCards = List.of(
                    PaymentCardDto.builder().id(10L).number("1111222233334444").build(),
                    PaymentCardDto.builder().id(11L).number("5555666677778888").build()
            );

            when(userClient.getAllPaymentCardsByUserId(userId)).thenReturn(mockCards);

            mockMvc.perform(get("/user/payment-cards")
                            .header(JWT_HEADER_NAME, bearerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(10L))
                    .andExpect(jsonPath("$[0].number").value("1111222233334444"))
                    .andExpect(jsonPath("$[1].id").value(11L));

            verify(userClient, times(1)).getAllPaymentCardsByUserId(userId);
        }

        @Test
        @DisplayName("Should return empty list when user has no cards")
        void shouldReturnEmptyListWhenNoCards() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);

            UserDto expectedDto = UserDto.builder()
                    .id(userId)
                    .name("Nikita")
                    .email("nikita@example.com")
                    .build();

            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;

            when(userClient.getAllPaymentCardsByUserId(userId)).thenReturn(List.of());

            mockMvc.perform(get("/user/payment-cards")
                            .header(JWT_HEADER_NAME, bearerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/user/payment-cards"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(userClient);
        }

        @Test
        @DisplayName("GET /user/payment-cards - Failure: should return 500 when UserClient fails")
        void shouldReturn500_WhenUserClientFails() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);

            UserDto expectedDto = UserDto.builder()
                    .id(userId)
                    .name("Nikita")
                    .email("nikita@example.com")
                    .build();

            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;

            when(userClient.getAllPaymentCardsByUserId(userId))
                    .thenThrow(new UserServiceException());

            mockMvc.perform(get("/user/payment-cards")
                            .header(JWT_HEADER_NAME, bearerToken))
                    .andExpect(status().isServiceUnavailable());

            verify(userClient, times(1)).getAllPaymentCardsByUserId(userId);
        }

        @Test
        @DisplayName("GET /user/payment-cards - Success: should verify full DTO fields")
        void shouldVerifyFullDtoContent() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);

            UserDto expectedDto = UserDto.builder()
                    .id(userId)
                    .name("Nikita")
                    .email("nikita@example.com")
                    .build();

            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;

            PaymentCardDto card = PaymentCardDto.builder()
                    .id(50L)
                    .number("4444")
                    .expirationDate(LocalDate.of(2028, 12, 31))
                    .build();

            when(userClient.getAllPaymentCardsByUserId(userId)).thenReturn(List.of(card));

            mockMvc.perform(get("/user/payment-cards")
                            .header(JWT_HEADER_NAME, bearerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].expirationDate").exists());

        }
    }

    @Nested
    @DisplayName("Update User Profile Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should successfully update user and return updated DTO")
        void shouldUpdateUserSuccessfully() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);



            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;

            UserDto requestDto = UserDto.builder()
                    .name("NewName")
                    .surname("NewSurname")
                    .email("new@example.com")
                    .build();

            UserDto responseDto = UserDto.builder()
                    .id(userId)
                    .name("NewName")
                    .surname("NewSurname")
                    .email("new@example.com")
                    .build();

            when(userClient.updateUser(userId, requestDto))
                    .thenReturn(responseDto);

            mockMvc.perform(put("/user/update")
                            .header(JWT_HEADER_NAME, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))

                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("NewName"))
                    .andExpect(jsonPath("$.surname").value("NewSurname"))
                    .andExpect(jsonPath("$.email").value("new@example.com"));


            verify(userClient, times(1)).updateUser(eq(userId), any(UserDto.class));
        }

        @Test
        @DisplayName("Should return 401 when trying to update without authentication")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            UserDto requestDto = UserDto.builder().name("Unauthorized").build();

            mockMvc.perform(put("/user/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(userClient);
        }

        @Test
        @DisplayName("PUT /user/update - Failure: should return 500 when UserClient fails")
        void shouldReturnInternalServerError_WhenUserClientFails() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);


            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;
            UserDto requestDto = UserDto.builder().name("Fail").build();

            when(userClient.updateUser(userId, requestDto))
                    .thenThrow(new UserServiceException());

            mockMvc.perform(put("/user/update")
                            .header(JWT_HEADER_NAME, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isServiceUnavailable());

            verify(userClient, times(1)).updateUser(eq(userId), any(UserDto.class));
        }

        @Test
        @DisplayName("PUT /user/update - Failure: should handle empty request body")
        void shouldReturnBadRequest_WhenBodyIsEmpty() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);



            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;

            mockMvc.perform(put("/user/update")
                            .header(JWT_HEADER_NAME, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PUT /user/update - Success: should update birthDate using LocalDate")
        void shouldUpdateBirthDateSuccessfully() throws Exception {
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("nikita_dev")
                    .password("password")
                    .role(Role.USER)
                    .build();

            userRepository.save(mockUser);


            String accessToken = jwtService.generateAccessToken(mockUser);
            String refreshToken = jwtService.generateRefreshToken(mockUser);

            Token token = new Token();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            token.setLoggedOut(false);
            token.setUser(mockUser);

            tokenRepository.save(token);

            String bearerToken = JWT_HEADER_PREFIX + accessToken;
            LocalDate birthDate = LocalDate.of(2008, 6, 27);

            UserDto requestDto = UserDto.builder()
                    .birthDate(birthDate)
                    .build();

            UserDto responseDto = UserDto.builder()
                    .id(userId)
                    .birthDate(birthDate)
                    .build();

            when(userClient.updateUser(userId, requestDto)).thenReturn(responseDto);

            mockMvc.perform(put("/user/update")
                            .header(JWT_HEADER_NAME, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.birthDate").exists());
        }
    }

}