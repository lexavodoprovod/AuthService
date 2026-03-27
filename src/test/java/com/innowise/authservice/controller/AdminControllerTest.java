package com.innowise.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.client.PaymentCardClient;
import com.innowise.authservice.client.UserClient;
import com.innowise.authservice.dto.PaymentCardDto;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.entity.Role;
import com.innowise.authservice.entity.User;
import com.innowise.authservice.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends BaseIT{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private PaymentCardClient paymentCardClient;


    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Get User By ID Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when admin requests by ID")
        void shouldReturnUserForAdmin() throws Exception {
            Long targetUserId = 50L;
            User adminPrincipal = User.builder()
                    .id(1L)
                    .username("admin_nikita")
                    .role(Role.ADMIN)
                    .build();

            UserDto expectedDto = UserDto.builder()
                    .id(targetUserId)
                    .name("Target User")
                    .email("target@example.com")
                    .build();

            when(userClient.getUserById(targetUserId)).thenReturn(expectedDto);

            mockMvc.perform(get("/admin/users/{id}", targetUserId)
                            .with(user(adminPrincipal)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(targetUserId))
                    .andExpect(jsonPath("$.name").value("Target User"));

            verify(userClient, times(1)).getUserById(targetUserId);
        }

        @Test
        @DisplayName("Should return 403 when regular user tries to access admin endpoint")
        void shouldReturn403ForRegularUser() throws Exception {
            User userPrincipal = User.builder()
                    .id(2L)
                    .username("regular_user")
                    .role(Role.USER)
                    .build();

            mockMvc.perform(get("/admin/users/50")
                            .with(user(userPrincipal)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(userClient);
        }

        @Test
        @DisplayName("Should return 404 when user not found in external service")
        void shouldReturn404WhenNotFound() throws Exception {
            Long nonExistentId = 999L;
            User admin = User.builder().role(Role.ADMIN).build();

            when(userClient.getUserById(nonExistentId))
                    .thenThrow(new UserNotFoundException("User not found"));

            mockMvc.perform(get("/admin/users/{id}", nonExistentId)
                            .with(user(admin)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Get User Cards by Admin Tests")
    class GetUserCardsTests {

        @Test
        @DisplayName("Should return list of cards for a specific user ID when requested by Admin")
        void shouldReturnUserCardsForAdmin() throws Exception {
            Long targetUserId = 44L;
            User admin = User.builder()
                    .id(1L)
                    .username("admin_boss")
                    .role(Role.ADMIN)
                    .build();

            List<PaymentCardDto> mockCards = List.of(
                    PaymentCardDto.builder().id(201L).number("4444555566667777").build(),
                    PaymentCardDto.builder().id(202L).number("1111000011110000").build()
            );

            when(userClient.getAllPaymentCardsByUserId(targetUserId)).thenReturn(mockCards);

            mockMvc.perform(get("/admin/users/{id}/cards", targetUserId)
                            .with(user(admin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].number").value("4444555566667777"))
                    .andExpect(jsonPath("$[1].id").value(202L));

            verify(userClient, times(1)).getAllPaymentCardsByUserId(targetUserId);
        }

        @Test
        @DisplayName("Should return 403 when regular user tries to view someone else's cards via admin endpoint")
        void shouldReturn403ForRegularUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();


            mockMvc.perform(get("/admin/users/44/cards")
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(userClient);
        }
    }

    @Nested
    @DisplayName("Get All Users with Pagination Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return page of users with filters for Admin")
        void shouldReturnUserPageForAdmin() throws Exception {
            User admin = User.builder().role(Role.ADMIN).build();

            UserDto user1 = UserDto.builder().id(1L).name("Ivan").surname("Ivanov").build();
            UserDto user2 = UserDto.builder().id(2L).name("Ivan").surname("Petrov").build();

            Page<UserDto> mockPage = new PageImpl<>(List.of(user1, user2),
                    PageRequest.of(0, 10), 2);

            when(userClient.getAllUsers(eq("Ivan"), eq(null), any(Pageable.class)))
                    .thenReturn(mockPage);

            mockMvc.perform(get("/admin/users")
                            .param("name", "Ivan")
                            .param("page", "0")
                            .param("size", "10")
                            .with(user(admin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].name").value("Ivan"))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(userClient).getAllUsers(eq("Ivan"), eq(null), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return 403 for non-admin users")
        void shouldReturn403ForUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();

            mockMvc.perform(get("/admin/users")
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Admin Update User Tests")
    class AdminUpdateUserTests {

        @Test
        @DisplayName("Should successfully update any user when requested by Admin")
        void shouldUpdateUserAsAdmin() throws Exception {
            Long targetUserId = 77L;
            User admin = User.builder()
                    .id(1L)
                    .username("admin_power")
                    .role(Role.ADMIN)
                    .build();

            UserDto requestDto = UserDto.builder()
                    .name("UpdatedName")
                    .email("updated@example.com")
                    .build();

            UserDto responseDto = UserDto.builder()
                    .id(targetUserId)
                    .name("UpdatedName")
                    .email("updated@example.com")
                    .build();

            when(userClient.updateUser(eq(targetUserId), any(UserDto.class)))
                    .thenReturn(responseDto);

            mockMvc.perform(put("/admin/users/{id}/update", targetUserId)
                            .with(user(admin))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(targetUserId))
                    .andExpect(jsonPath("$.name").value("UpdatedName"));

            verify(userClient).updateUser(eq(targetUserId), any(UserDto.class));
        }

        @Test
        @DisplayName("Should return 403 when regular user tries to update someone via admin path")
        void shouldReturn403ForRegularUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();
            UserDto dto = UserDto.builder().name("Hack").build();

            mockMvc.perform(put("/admin/users/77/update")
                            .with(user(regularUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(userClient);
        }
    }

    @Nested
    @DisplayName("Activate User Tests")
    class ActivateUserTests {

        @Test
        @DisplayName("Should successfully activate user when requested by Admin")
        void shouldActivateUserAsAdmin() throws Exception {
            Long targetUserId = 10L;
            User admin = User.builder()
                    .id(1L)
                    .username("admin_power")
                    .role(Role.ADMIN)
                    .build();

            when(userClient.activateUser(targetUserId))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/admin/users/{id}/activate", targetUserId)
                            .with(user(admin)))
                    .andExpect(status().isOk());

            verify(userClient, times(1)).activateUser(targetUserId);
        }

        @Test
        @DisplayName("Should return 403 when regular user tries to activate someone")
        void shouldReturn403ForRegularUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();

            mockMvc.perform(patch("/admin/users/10/activate")
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(userClient);
        }
    }

    @Nested
    @DisplayName("Deactivate User Tests")
    class DeactivateUserTests {

        @Test
        @DisplayName("Should successfully deactivate user when requested by Admin")
        void shouldDeactivateUserAsAdmin() throws Exception {
            Long targetUserId = 25L;
            User admin = User.builder()
                    .id(1L)
                    .username("admin_moderator")
                    .role(Role.ADMIN)
                    .build();

            when(userClient.deactivateUser(targetUserId))
                    .thenReturn(ResponseEntity.noContent().build());

            mockMvc.perform(delete("/admin/users/{id}", targetUserId)
                            .with(user(admin)))
                    .andExpect(status().isNoContent());

            verify(userClient, times(1)).deactivateUser(targetUserId);
        }

        @Test
        @DisplayName("Should return 403 when regular user tries to deactivate someone")
        void shouldReturn403ForRegularUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();

            mockMvc.perform(delete("/admin/users/25")
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(userClient);
        }
    }

    @Nested
    @DisplayName("Get All Payment Cards Tests")
    class GetAllCardsTests {

        @Test
        @DisplayName("Should return page of cards for Admin with number filter")
        void shouldReturnCardsPageForAdmin() throws Exception {
            User admin = User.builder()
                    .role(Role.ADMIN)
                    .build();

            String cardNumberFilter = "4444";

            PaymentCardDto card = PaymentCardDto.builder()
                    .id(100L)
                    .number("4444555566667777")
                    .build();

            Page<PaymentCardDto> mockPage = new PageImpl<>(List.of(card),
                    PageRequest.of(0, 10), 1);

            when(paymentCardClient.getAllPaymentCards(eq(cardNumberFilter), any(Pageable.class)))
                    .thenReturn(mockPage);

            mockMvc.perform(get("/admin/cards")
                            .param("number", cardNumberFilter)
                            .param("page", "0")
                            .param("size", "10")
                            .with(user(admin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].number").value("4444555566667777"))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(paymentCardClient).getAllPaymentCards(eq(cardNumberFilter), any(Pageable.class));
        }

        @Test
        @DisplayName("Should return 403 when regular user tries to list all cards")
        void shouldReturn403ForUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();

            mockMvc.perform(get("/admin/cards")
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(paymentCardClient);
        }
    }

    @Nested
    @DisplayName("Get Payment Card By ID Tests")
    class GetPaymentCardByIdTests {

        @Test
        @DisplayName("Should return payment card when Admin requests by ID")
        void shouldReturnCardForAdmin() throws Exception {
            Long cardId = 100L;
            User admin = User.builder()
                    .id(1L)
                    .role(Role.ADMIN)
                    .build();

            PaymentCardDto expectedCard = PaymentCardDto.builder()
                    .id(cardId)
                    .number("4444555566667777")
                    .holder("NIKITA PRO")
                    .build();

            when(paymentCardClient.getPaymentCardById(cardId)).thenReturn(expectedCard);

            mockMvc.perform(get("/admin/cards/{id}", cardId)
                            .with(user(admin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(cardId))
                    .andExpect(jsonPath("$.number").value("4444555566667777"))
                    .andExpect(jsonPath("$.holder").value("NIKITA PRO"));

            verify(paymentCardClient, times(1)).getPaymentCardById(cardId);
        }

        @Test
        @DisplayName("Should return 403 when regular user tries to access this card endpoint")
        void shouldReturn403ForUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();

            mockMvc.perform(get("/admin/cards/100")
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(paymentCardClient);
        }
    }

    @Nested
    @DisplayName("Update Payment Card Tests")
    class UpdatePaymentCardTests {

        @Test
        @DisplayName("Should successfully update payment card when requested by Admin")
        void shouldUpdateCardAsAdmin() throws Exception {
            Long cardId = 55L;
            User admin = User.builder()
                    .id(1L)
                    .role(Role.ADMIN)
                    .build();

            PaymentCardDto requestDto = PaymentCardDto.builder()
                    .number("5555666677778888")
                    .holder("Updated Owner")
                    .build();

            PaymentCardDto responseDto = PaymentCardDto.builder()
                    .id(cardId)
                    .number("5555666677778888")
                    .holder("Updated Owner")
                    .build();


            when(paymentCardClient.updatePaymentCard(eq(cardId), any(PaymentCardDto.class)))
                    .thenReturn(responseDto);

            mockMvc.perform(put("/admin/cards/{id}/update", cardId)
                            .with(user(admin))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(cardId))
                    .andExpect(jsonPath("$.holder").value("Updated Owner"))
                    .andExpect(jsonPath("$.number").value("5555666677778888"));

            verify(paymentCardClient, times(1)).updatePaymentCard(eq(cardId), any(PaymentCardDto.class));
        }
    }

    @Nested
    @DisplayName("Activate Payment Card Tests")
    class ActivateCardTests {

        @Test
        @DisplayName("Should successfully activate payment card when requested by Admin")
        void shouldActivateCardAsAdmin() throws Exception {
            Long cardId = 99L;
            User admin = User.builder()
                    .id(1L)
                    .role(Role.ADMIN)
                    .build();

            when(paymentCardClient.activatePaymentCard(cardId))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(patch("/admin/cards/{id}/activate", cardId)
                            .with(user(admin)))
                    .andExpect(status().isOk());

            verify(paymentCardClient, times(1)).activatePaymentCard(cardId);
        }

        @Test
        @DisplayName("Should return 403 for non-admin user")
        void shouldReturn403ForUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();

            mockMvc.perform(patch("/admin/cards/99/activate")
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Deactivate Payment Card Tests")
    class DeactivateCardTests {

        @Test
        @DisplayName("Should successfully deactivate payment card when requested by Admin")
        void shouldDeactivateCardAsAdmin() throws Exception {
            Long cardId = 123L;
            User admin = User.builder()
                    .id(1L)
                    .username("super_admin")
                    .role(Role.ADMIN)
                    .build();

            when(paymentCardClient.deactivatePaymentCard(cardId))
                    .thenReturn(ResponseEntity.noContent().build());

            mockMvc.perform(delete("/admin/cards/{id}", cardId)
                            .with(user(admin)))
                    .andExpect(status().isNoContent());

            verify(paymentCardClient, times(1)).deactivatePaymentCard(cardId);
        }

        @Test
        @DisplayName("Should return 403 when a regular user tries to deactivate a card")
        void shouldReturn403ForRegularUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();

            mockMvc.perform(delete("/admin/cards/123")
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(userClient);
        }
    }

}