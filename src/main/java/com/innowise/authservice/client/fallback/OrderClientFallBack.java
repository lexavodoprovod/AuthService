package com.innowise.authservice.client.fallback;

import com.innowise.authservice.client.OrderClient;
import com.innowise.authservice.dto.request.OrderRequestDto;
import com.innowise.authservice.dto.response.OrderResponseDto;
import com.innowise.authservice.entity.OrderStatus;
import com.innowise.authservice.exception.OrderServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class OrderClientFallBack implements OrderClient{
    @Override
    public OrderResponseDto addOrder(OrderRequestDto orderDto) {
        throw new OrderServiceException();
    }

    @Override
    public OrderResponseDto getOrderById(Long id) {
        throw new OrderServiceException();
    }

    @Override
    public Page<OrderResponseDto> getOrdersByUserId(Long id, Pageable pageable) {
        throw new OrderServiceException();
    }

    @Override
    public Page<OrderResponseDto> getAllOrders(LocalDate from, LocalDate to, List<OrderStatus> statuses, Pageable pageable) {
        throw new OrderServiceException();
    }

    @Override
    public OrderResponseDto updateStatus(Long id, OrderStatus status) {
        throw new OrderServiceException();
    }

    @Override
    public OrderResponseDto updateOrder(Long id, OrderRequestDto orderDto) {
        throw new OrderServiceException();
    }

    @Override
    public ResponseEntity<Void> deleteOrder(Long id) {
        throw new OrderServiceException();
    }
}
