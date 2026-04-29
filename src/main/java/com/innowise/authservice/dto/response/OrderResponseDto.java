package com.innowise.authservice.dto.response;

import com.innowise.authservice.dto.UserDto;
import com.innowise.authservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {

    private Long id;

    private UserDto userDto;

    private OrderStatus status;

    private Long totalPrice;

    private boolean deleted;

    private List<OrderItemResponseDto> orderItems;
}