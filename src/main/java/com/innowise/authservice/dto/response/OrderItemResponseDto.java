package com.innowise.authservice.dto.response;

import com.innowise.authservice.dto.request.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDto {
    private Long id;

    private ItemDto itemDto;

    private int quantity;
}
