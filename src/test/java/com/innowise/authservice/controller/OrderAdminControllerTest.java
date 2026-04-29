package com.innowise.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.client.OrderClient;
import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.dto.request.OrderRequestDto;
import com.innowise.authservice.dto.response.OrderResponseDto;
import com.innowise.authservice.entity.OrderStatus;
import com.innowise.authservice.entity.Role;
import com.innowise.authservice.entity.User;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class OrderAdminControllerTest extends BaseIT{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderClient orderClient;

    @Autowired
    private ObjectMapper objectMapper;


    @Nested
    @DisplayName("Tests for addOrder method")
    class AddOrderTests {

        @Test
        @DisplayName("Should create order successfully for Admin")
        void shouldCreateOrderForAdmin() throws Exception {
            User admin = User.builder().role(Role.ADMIN).build();
            OrderRequestDto inputDto = new OrderRequestDto(1L, List.of());
            OrderResponseDto savedDto = OrderResponseDto.builder()
                    .id(1L)
                    .userDto(new UserDto())
                    .build();

            when(orderClient.addOrder(any(OrderRequestDto.class))).thenReturn(savedDto);

            mockMvc.perform(post("/admin/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto))
                            .with(user(admin)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(orderClient).addOrder(any(OrderRequestDto.class));
        }

        @Test
        @DisplayName("Should return 403 Forbidden for non-admin users")
        void shouldReturn403ForRegularUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();

            mockMvc.perform(post("/admin/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(orderClient);
        }
    }

    @Nested
    @DisplayName("Tests for getAllOrders method")
    class GetAllOrdersTests {

        @Test
        @DisplayName("Should return page of orders for Admin with parameters")
        void shouldReturnOrderPageForAdmin() throws Exception {
            User admin = User.builder().role(Role.ADMIN).build();

            OrderResponseDto order1 = OrderResponseDto.builder().id(101L).build();
            OrderResponseDto order2 = OrderResponseDto.builder().id(102L).build();

            Page<OrderResponseDto> mockPage = new PageImpl<>(
                    List.of(order1, order2),
                    PageRequest.of(0, 10),
                    2
            );

            when(orderClient.getAllOrders(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(mockPage);

            mockMvc.perform(get("/admin/orders")
                            .with(user(admin))
                            .param("from", "2026-01-01")
                            .param("to", "2026-12-31")
                            .param("statuses", "NEW,PAID")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].id").value(101))
                    .andExpect(jsonPath("$.content[1].id").value(102))
                    .andExpect(jsonPath("$.totalElements").value(2));

            verify(orderClient).getAllOrders(
                    eq(LocalDate.of(2026, 1, 1)),
                    eq(LocalDate.of(2026, 12, 31)),
                    eq(List.of(OrderStatus.NEW, OrderStatus.PAID)),
                    any(Pageable.class)
            );
        }

        @Test
        @DisplayName("Should return 403 Forbidden for regular User")
        void shouldReturn403ForUser() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();

            mockMvc.perform(get("/admin/orders")
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(orderClient);
        }
    }

    @Nested
    @DisplayName("Tests for updateOrder method")
    class UpdateOrderTests {

        @Test
        @DisplayName("Should update order successfully for Admin")
        void shouldUpdateOrderForAdmin() throws Exception {
            User admin = User.builder().role(Role.ADMIN).build();
            Long orderId = 100L;

            OrderRequestDto updateRequest = new OrderRequestDto(orderId, List.of());

            OrderResponseDto updatedResponse = OrderResponseDto.builder()
                    .id(orderId)
                    .status(OrderStatus.NEW)
                    .build();

            when(orderClient.updateOrder(eq(orderId), any(OrderRequestDto.class)))
                    .thenReturn(updatedResponse);

            mockMvc.perform(put("/admin/orders/{id}", orderId)
                            .with(user(admin))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(orderId))
                    .andExpect(jsonPath("$.status").value("NEW"));

            verify(orderClient).updateOrder(eq(orderId), any(OrderRequestDto.class));
        }

        @Test
        @DisplayName("Should return 403 Forbidden for User when updating")
        void shouldReturn403ForUserOnUpdate() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();
            Long orderId = 100L;
            OrderRequestDto updateRequest = new OrderRequestDto(orderId, List.of());

            mockMvc.perform(put("/admin/orders/{id}", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest))
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(orderClient);
        }
    }

    @Nested
    @DisplayName("Tests for updateStatus method")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update status successfully for Admin")
        void shouldUpdateStatusForAdmin() throws Exception {
            User admin = User.builder().role(Role.ADMIN).build();
            Long orderId = 100L;
            OrderStatus newStatus = OrderStatus.PAID;

            OrderResponseDto updatedResponse = OrderResponseDto.builder()
                    .id(orderId)
                    .status(newStatus)
                    .build();

            when(orderClient.updateStatus(eq(orderId), eq(newStatus)))
                    .thenReturn(updatedResponse);

            mockMvc.perform(patch("/admin/orders/{id}/status", orderId)
                            .with(user(admin))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newStatus)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(orderId))
                    .andExpect(jsonPath("$.status").value("PAID"));

            verify(orderClient).updateStatus(eq(orderId), eq(newStatus));
        }

        @Test
        @DisplayName("Should return 403 Forbidden for User when updating status")
        void shouldReturn403ForUserOnUpdateStatus() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();
            Long orderId = 100L;
            OrderStatus newStatus = OrderStatus.CANCELLED;

            mockMvc.perform(patch("/admin/orders/{id}/status", orderId)
                            .with(user(regularUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newStatus)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(orderClient);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when status is invalid")
        void shouldReturn400ForInvalidStatus() throws Exception {
            User admin = User.builder().role(Role.ADMIN).build();
            Long orderId = 100L;

            mockMvc.perform(patch("/admin/orders/{id}/status", orderId)
                            .with(user(admin))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("\"INVALID_STATUS\""))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for deleteOrder method")
    class DeleteOrderTests {

        @Test
        @DisplayName("Should delete order successfully for Admin")
        void shouldDeleteOrderForAdmin() throws Exception {
            User admin = User.builder().role(Role.ADMIN).build();
            Long orderId = 500L;


            when(orderClient.deleteOrder(orderId))
                    .thenReturn(ResponseEntity.noContent().build());

            mockMvc.perform(delete("/admin/orders/{id}", orderId)
                            .with(user(admin)))
                    .andExpect(status().isNoContent());

            verify(orderClient).deleteOrder(orderId);
        }

        @Test
        @DisplayName("Should return 403 Forbidden for User when deleting")
        void shouldReturn403ForUserOnDelete() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();
            Long orderId = 500L;

            mockMvc.perform(delete("/admin/orders/{id}", orderId)
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(orderClient);
        }
    }


}