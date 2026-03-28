package com.innowise.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.client.OrderClient;
import com.innowise.authservice.dto.OrderDto;
import com.innowise.authservice.entity.Role;
import com.innowise.authservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
            OrderDto inputDto = new OrderDto(1L);
            OrderDto savedDto = new OrderDto(1L);

            when(orderClient.addOrder(any(OrderDto.class))).thenReturn(savedDto);

            mockMvc.perform(post("/admin/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto))
                            .with(user(admin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(orderClient).addOrder(any(OrderDto.class));
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
        @DisplayName("Should return page of orders for Admin")
        void shouldReturnOrderPageForAdmin() throws Exception {
            User admin = User.builder().role(Role.ADMIN).build();

            OrderDto order1 = new OrderDto(101L);
            OrderDto order2 = new OrderDto(102L);

            Page<OrderDto> mockPage = new PageImpl<>(List.of(order1, order2),
                    PageRequest.of(0, 10), 2);

            when(orderClient.getAllOrders()).thenReturn(mockPage);

            mockMvc.perform(get("/admin/orders")
                            .with(user(admin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].id").value(101L))
                    .andExpect(jsonPath("$.content[1].id").value(102L))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(orderClient).getAllOrders();
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

            OrderDto updateRequest = new OrderDto(orderId);
            OrderDto updatedResponse = new OrderDto(orderId);

            when(orderClient.updateOrder(eq(orderId), any(OrderDto.class)))
                    .thenReturn(updatedResponse);

            mockMvc.perform(put("/admin/orders/{id}", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest))
                            .with(user(admin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(orderId));

            verify(orderClient).updateOrder(eq(orderId), any(OrderDto.class));
        }

        @Test
        @DisplayName("Should return 403 Forbidden for User when updating")
        void shouldReturn403ForUserOnUpdate() throws Exception {
            User regularUser = User.builder().role(Role.USER).build();
            Long orderId = 100L;
            OrderDto updateRequest = new OrderDto(orderId);

            mockMvc.perform(put("/admin/orders/{id}", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest))
                            .with(user(regularUser)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(orderClient);
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