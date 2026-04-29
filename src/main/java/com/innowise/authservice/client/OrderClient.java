package com.innowise.authservice.client;

import com.innowise.authservice.client.fallback.OrderClientFallBack;
import com.innowise.authservice.dto.request.OrderRequestDto;
import com.innowise.authservice.dto.response.OrderResponseDto;
import com.innowise.authservice.entity.OrderStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.innowise.authservice.constant.PaginationSettings.*;

@FeignClient(name = "order-service",
        contextId = "orderClient",
        path = "/orders",
        fallback = OrderClientFallBack.class)
public interface OrderClient {

    @PostMapping
    OrderResponseDto addOrder(@RequestBody OrderRequestDto orderDto);

    @GetMapping("/{id}")
    OrderResponseDto getOrderById(@PathVariable Long id);

    @GetMapping("/user/{id}")
    Page<OrderResponseDto> getOrdersByUserId(
            @PathVariable Long id,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable);

    @GetMapping
    Page<OrderResponseDto> getAllOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) List<OrderStatus> statuses,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable);

    @PutMapping("/{id}")
    OrderResponseDto updateOrder(@PathVariable Long id, @RequestBody OrderRequestDto orderDto);

    @PatchMapping("/{id}/status")
    OrderResponseDto updateStatus(
            @PathVariable Long id,
            @RequestBody OrderStatus status);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteOrder(@PathVariable Long id);
}
