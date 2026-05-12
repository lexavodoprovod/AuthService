package com.innowise.authservice.client.fallback;

import com.innowise.authservice.client.OrderClient;
import com.innowise.authservice.dto.request.OrderRequestDto;
import com.innowise.authservice.dto.response.OrderResponseDto;
import com.innowise.authservice.entity.OrderStatus;
import com.innowise.authservice.exception.callbackexceptions.OrderServiceException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import static com.innowise.authservice.client.ThrowFeignException.*;

@Component
public class OrderClientFallBackFactory implements FallbackFactory<OrderClient> {
    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {
            @Override
            public OrderResponseDto addOrder(OrderRequestDto orderDto) {
                throwFeignEx(cause);
                throw new OrderServiceException();
            }

            @Override
            public OrderResponseDto getOrderById(Long id) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }

            @Override
            public Page<OrderResponseDto> getOrdersByUserId(Long id, Pageable pageable) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }

            @Override
            public Page<OrderResponseDto> getAllOrders(LocalDate from, LocalDate to, List<OrderStatus> statuses, Pageable pageable) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }

            @Override
            public OrderResponseDto updateOrder(Long id, OrderRequestDto orderDto) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }

            @Override
            public OrderResponseDto updateStatus(Long id, OrderStatus status) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }

            @Override
            public ResponseEntity<Void> deleteOrder(Long id) {
                throwFeignEx(cause);
                throw new OrderServiceException();            }
        };
    }
}
