package com.innowise.authservice.client.fallback;

import com.innowise.authservice.client.ItemClient;
import com.innowise.authservice.client.OrderClient;
import com.innowise.authservice.dto.ItemDto;
import com.innowise.authservice.dto.OrderDto;
import com.innowise.authservice.exception.OrderServiceException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderClientFallBack implements OrderClient, ItemClient {
    @Override
    public OrderDto addOrder(OrderDto orderDto) {
        throw new OrderServiceException();
    }

    @Override
    public Page<OrderDto> getAllOrders() {
        throw new OrderServiceException();
    }

    @Override
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        throw new OrderServiceException();
    }

    @Override
    public ResponseEntity<Void> deleteOrder(Long id) {
        throw new OrderServiceException();
    }

    @Override
    public ItemDto addItem(ItemDto itemDto) {
        throw new OrderServiceException();
    }

    @Override
    public Page<ItemDto> getAllItems() {
        throw new OrderServiceException();
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto) {
        throw new OrderServiceException();
    }

    @Override
    public ResponseEntity<Void> deleteItem(Long id) {
        throw new OrderServiceException();
    }
}
