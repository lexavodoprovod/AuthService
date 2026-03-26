package com.innowise.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.entity.Role;
import com.innowise.authservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseIT{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserClient userClient;

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
                    .role(Role.USER)
                    .build();

            UserDto expectedDto = UserDto.builder()
                    .id(userId)
                    .name("Nikita")
                    .email("nikita@example.com")
                    .build();

            when(userClient.getUserById(userId)).thenReturn(expectedDto);

            mockMvc.perform(get("/user/profile")
                            .with(user(mockUser)))
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
                    .username("card_holder")
                    .role(Role.USER)
                    .build();

            List<PaymentCardDto> mockCards = List.of(
                    PaymentCardDto.builder().id(10L).number("1111222233334444").build(),
                    PaymentCardDto.builder().id(11L).number("5555666677778888").build()
            );

            when(userClient.getAllPaymentCardsByUserId(userId)).thenReturn(mockCards);

            mockMvc.perform(get("/user/payment-cards")
                            .with(user(mockUser)))
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
            Long userId = 2L;
            User mockUser = User.builder().id(userId).username("no_cards_user").build();

            when(userClient.getAllPaymentCardsByUserId(userId)).thenReturn(List.of());

            mockMvc.perform(get("/user/payment-cards")
                            .with(user(mockUser)))
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
                    .role(Role.USER)
                    .build();

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

            when(userClient.updateUser(eq(userId), any(UserDto.class)))
                    .thenReturn(responseDto);

            mockMvc.perform(put("/user/update")
                            .with(user(mockUser))
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
    }

}