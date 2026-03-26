package com.innowise.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.LoginDto;
import com.innowise.authservice.dto.RegistrationDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthenticationControllerTest extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserClient userClient;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;



    @BeforeEach
    void setUp() {
        tokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("Register Integration Tests")
    class RegisterIntegrationTests{
        @Test
        @DisplayName("POST /auth/registration should return 200 and save user")
        void shouldRegisterUserSuccessfully() throws Exception {
            RegistrationDto registrationDto = RegistrationDto.builder()
                    .username("nikita_user")
                    .password("password123")
                    .name("Nikita")
                    .email("nikita@example.com")
                    .build();

            UserDto feignResponse = UserDto.builder()
                    .id(101L)
                    .build();

            when(userClient.addUser(any(UserDto.class))).thenReturn(feignResponse);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/registration")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registrationDto))
                            .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());



            Optional<User> savedUser = userRepository.findByUsername("nikita_user");
            assertTrue(savedUser.isPresent(), "User should be saved in the database");
            assertEquals(101L, savedUser.get().getId(), "Saved user should have the ID returned by Feign");
        }

        @Test
        @DisplayName("POST /auth/registration should return 400 when user already exists")
        void shouldReturnErrorWhenUserExists() throws Exception {

            User user = User.builder()
                    .id(101L)
                    .username("existing_nikita")
                    .password("123")
                    .role(Role.USER)
                    .build();

            userRepository.save(user);

            RegistrationDto registrationDto = RegistrationDto.builder()
                    .username("existing_nikita")
                    .password("password123")
                    .build();

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/registration")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registrationDto))
                            .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("POST /auth/registration should return 400 when request body is empty or invalid")
        void shouldReturnBadRequest_WhenDtoIsInvalid() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/registration")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content(""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /auth/registration should return 500 when UserClient (Feign) fails")
        void shouldReturnInternalServerError_WhenFeignClientFails() throws Exception {
            RegistrationDto registrationDto = RegistrationDto.builder()
                    .username("feign_fail_user")
                    .password("pass")
                    .build();

            when(userClient.addUser(any(UserDto.class)))
                    .thenThrow(new UserServiceException());

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/registration")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registrationDto)))
                    .andExpect(status().isServiceUnavailable());

            Optional<User> savedUser = userRepository.findByUsername("feign_fail_user");
            assertFalse(savedUser.isPresent());
        }

    }

    @Nested
    @DisplayName("Login endpoint tests")
    class LoginTests {

        @Test
        @DisplayName("POST /auth/login - Success: should return 200 and JWT tokens")
        void login_Success() throws Exception {
            String username = "danik_pro";
            String rawPassword = "password123";

            User user = User.builder()
                    .id(101L)
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .role(Role.USER)
                    .build();

            userRepository.save(user);

            LoginDto loginDto = new LoginDto(username, rawPassword);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/login")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto)))
                    .andExpect(status().isOk())
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.refreshToken").exists());


            List<Token> tokens = tokenRepository.findAllAccessTokensByUserId(user.getId());
            assertFalse(tokens.isEmpty(), "Token should be persisted in DB after login");
            assertFalse(tokens.get(0).isLoggedOut());
        }

        @Test
        @DisplayName("POST /auth/login - Failure: should return 403 when password is wrong")
        void login_WrongPassword() throws Exception {
            User user = User.builder()
                    .id(101L)
                    .username("nikita")
                    .password(passwordEncoder.encode("correct_pass"))
                    .build();

            userRepository.save(user);

            LoginDto loginDto = new LoginDto("nikita", "wrong_pass");


            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/login")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto))
                            .accept(org.springframework.http.MediaType.APPLICATION_JSON))

                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /auth/login - Failure: should return 404 when user not found")
        void login_UserNotFound() throws Exception {
            LoginDto loginDto = new LoginDto("ghost_user", "any_pass");

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/login")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto))
                            .accept(org.springframework.http.MediaType.APPLICATION_JSON))

                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /auth/login - Failure: should return 400 if loginDto is null")
        void login_NullDto() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/login")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Refresh token endpoint tests")
    class RefreshTokenTests {

        @Test
        @DisplayName("POST /auth/refresh_token - Success: should rotate tokens")
        void refreshToken_Success() throws Exception {
            String username = "refresh_user";
            User user = User.builder()
                    .id(101L)
                    .username(username)
                    .password("pass")
                    .role(Role.USER)
                    .build();
            userRepository.save(user);

            String oldAccessToken = jwtService.generateAccessToken(user);
            String oldRefreshToken = jwtService.generateRefreshToken(user);


            Token tokenEntity = new Token();
            tokenEntity.setAccessToken(oldAccessToken);
            tokenEntity.setRefreshToken(oldRefreshToken);
            tokenEntity.setUser(user);
            tokenEntity.setLoggedOut(false);
            tokenRepository.save(tokenEntity);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/refresh_token")
                            .header("Authorization", "Bearer " + oldRefreshToken))
                    .andExpect(status().isOk());

            Token updatedOldToken = tokenRepository.findTokenByAccessToken(oldAccessToken).orElseThrow();

            assertTrue(updatedOldToken.isLoggedOut(), "Old token should be revoked");
        }

        @Test
        @DisplayName("POST /auth/refresh_token - Failure: should return 401 when token is revoked")
        void refreshToken_RevokedToken() throws Exception {
            User user = User.builder()
                    .id(1L)
                    .username("hacker").
                    password("123").
                    build();

            userRepository.save(user);

            String oldAccessToken = jwtService.generateAccessToken(user);
            String oldRefreshToken = jwtService.generateRefreshToken(user);

            Token revokedToken = new Token();
            revokedToken.setAccessToken(oldAccessToken);
            revokedToken.setRefreshToken(oldRefreshToken);
            revokedToken.setUser(user);
            revokedToken.setLoggedOut(true);
            tokenRepository.save(revokedToken);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/refresh_token")
                            .header("Authorization", "Bearer " + oldRefreshToken))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /auth/refresh_token - Failure: should return 401 when header is missing")
        void refreshToken_NoHeader() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                    .post("/auth/refresh_token"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /auth/refresh_token - Failure: should return 401 when Bearer prefix is missing")
        void refreshToken_MissingBearerPrefix() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/refresh_token")
                            .header("Authorization", "JustSomeRandomTokenString"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /auth/refresh_token - Success: should save new access token to database")
        void refreshToken_VerifyNewTokenSaved() throws Exception {
            User user = User.builder().id(101L).username("rotate_user").password("123").role(Role.USER).build();
            userRepository.save(user);
            String oldRefreshToken = jwtService.generateRefreshToken(user);

            Token tokenEntity = new Token();
            tokenEntity.setRefreshToken(oldRefreshToken);
            tokenEntity.setAccessToken(jwtService.generateAccessToken(user));
            tokenEntity.setUser(user);
            tokenEntity.setLoggedOut(false);
            tokenRepository.save(tokenEntity);

            var response = mockMvc.perform(MockMvcRequestBuilders
                            .post("/auth/refresh_token")
                            .header("Authorization", "Bearer " + oldRefreshToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String newAccessToken = com.jayway.jsonpath.JsonPath.read(response, "$.accessToken");

            Optional<Token> newTokenInDb = tokenRepository.findTokenByAccessToken(newAccessToken);
            assertTrue(newTokenInDb.isPresent(), "New access token must be persisted in DB");
            assertFalse(newTokenInDb.get().isLoggedOut(), "New token must be active");
        }

    }


}